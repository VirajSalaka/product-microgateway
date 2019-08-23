package org.wso2.apimgt.gateway.cli.protobuf.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.cli.exception.GrpcCodeGenException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.ballerinalang.net.grpc.proto.ServiceProtoConstants.TMP_DIRECTORY_PATH;
import static org.wso2.apimgt.gateway.cli.protobuf.ProtobufConstants.*;

/**
 * Class to implement "grpc" command for ballerina.
 * Ex: ballerina grpc  --proto_path (proto-file-path)  --exe_path (protoc-executor-path)
 */
public class GrpcCmd {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcCmd.class);
    
    private static final PrintStream outStream = System.out;

    private String protoPath;
    
    private String protocExePath;

    //todo: check on possibility to bundle inside the pack rather than downloading
    private String protocVersion = "3.7.0";

    public void execute() {
        // check input protobuf file path
        //todo: introduce constant for the extension
        if (protoPath == null || !protoPath.toLowerCase(Locale.ENGLISH).endsWith(PROTO_SUFFIX)) {
            String errorMessage = "Invalid proto file path. Please input valid proto file location.";
            outStream.println(errorMessage);
            return;
        }
        if (!Files.isReadable(Paths.get(protoPath))) {
            String errorMessage = "Provided service proto file is not readable. Please input valid proto file " +
                    "location.";
            outStream.println(errorMessage);
            return;
        }

        // read root/dependent file descriptors.
        File descFile = createTempDirectory();
        StringBuilder msg = new StringBuilder();
        LOG.debug("Initializing the ballerina code generation.");
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            List<String> protoFiles = readProperties(classLoader);
            for (String file : protoFiles) {
                try {
                    exportResource(file, classLoader);
                } catch (Exception e) {
                    msg.append("Error extracting resource file ").append(file).append(NEW_LINE_CHARACTER);
                    LOG.error("Error extracting resource file ", e);
                }
            }
            if (msg.toString().isEmpty()) {
                outStream.println("Successfully extracted library files.");
            } else {
                outStream.println(msg.toString());
                return;
            }
            try {
                DescriptorsGenerator.generateRootFileDescriptor(this.protocExePath, new File(protoPath)
                        .getAbsolutePath(), descFile.getAbsolutePath());
            } catch (GrpcCodeGenException e) {
                String errorMessage = "Error occurred when generating proto descriptor. " + e.getMessage();
                LOG.error("Error occurred when generating proto descriptor.", e);
                outStream.println(errorMessage);
                return;
            }
//            if (root.length == 0) {
//                String errorMsg = "Error occurred when generating proto descriptor.";
//                LOG.error(errorMsg);
//                outStream.println(errorMsg);
//                return;
//            }
            LOG.debug("Successfully generated root descriptor.");
        } finally {
            //delete temporary meta files
            File tempDir = new File(TMP_DIRECTORY_PATH);
            delete(new File(tempDir, META_LOCATION));
            delete(new File(tempDir, TEMP_GOOGLE_DIRECTORY));
            LOG.debug("Successfully deleted temporary files.");
        }
    }
    
    /**
     * Create meta temp directory which needed for intermediate processing.
     *
     * @return Temporary Created meta file.
     */
    private File createTempDirectory() {
        File parent = new File(TMP_DIRECTORY_PATH);
        File metadataHome = new File(parent, META_LOCATION);
        if (!metadataHome.exists() && !metadataHome.mkdir()) {
            throw new IllegalStateException("Couldn't create dir: " + metadataHome);
        }
        File googleHome = new File(parent, TEMP_GOOGLE_DIRECTORY);
        createTempDirectory(googleHome);
        File protobufHome = new File(googleHome, TEMP_PROTOBUF_DIRECTORY);
        createTempDirectory(protobufHome);
        File compilerHome = new File(protobufHome, TEMP_COMPILER_DIRECTORY);
        createTempDirectory(compilerHome);
        return new File(metadataHome, getProtoFileName() + "-descriptor.desc");
    }

    private void createTempDirectory(File dirName) {
        if (!dirName.exists() && !dirName.mkdir()) {
            throw new IllegalStateException("Couldn't create dir: " + dirName);
        }
    }

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/wrapper.proto"
     */
    private static void exportResource(String resourceName, ClassLoader classLoader) {
        try (InputStream initialStream = classLoader.getResourceAsStream(resourceName);
             OutputStream resStreamOut = new FileOutputStream(new File(TMP_DIRECTORY_PATH, resourceName.replace
                     ("stdlib", "protobuf")))) {
            if (initialStream == null) {
                throw new GrpcCodeGenException("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = initialStream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            throw new GrpcCodeGenException("Cannot find '" + resourceName + "' resource  at the jar.", e);
        }
    }

    private String getProtoFileName() {
        File file = new File(protoPath);
        return file.getName().replace(PROTO_SUFFIX, EMPTY_STRING);
    }
    
    private List<String> readProperties(ClassLoader classLoader) {
        String fileName;
        List<String> protoFilesList = new ArrayList<>();
        try (InputStream initialStream = classLoader.getResourceAsStream("standardProtos.properties");
             BufferedReader reader = new BufferedReader(new InputStreamReader(initialStream, StandardCharsets.UTF_8))) {
            while ((fileName = reader.readLine()) != null) {
                protoFilesList.add(fileName);
            }
        } catch (IOException e) {
            throw new GrpcCodeGenException("Error in reading standardProtos.properties.", e);
        }
        return protoFilesList;
    }
    
    public void setProtoPath(String protoPath) {
        this.protoPath = protoPath;
    }

    /**
     * Used to clear the temporary files.
     *
     * @param file file to be deleted
     */
    public static void delete(File file) {
        if ((file != null) && file.exists() && file.isDirectory()) {
            String[] files = file.list();
            if (files != null) {
                if (files.length != 0) {
                    for (String temp : files) {
                        File fileDelete = new File(file, temp);
                        if (fileDelete.isDirectory()) {
                            delete(fileDelete);
                        }
                        if (fileDelete.delete()) {
                            LOG.debug("Successfully deleted file " + file.toString());
                        }
                    }
                }
            }
            if (file.delete()) {
                LOG.debug("Successfully deleted file " + file.toString());
            }
            if ((file.getParentFile() != null) && (file.getParentFile().delete())) {
                LOG.debug("Successfully deleted parent file " + file.toString());
            }
        } else if (file != null) {
            if (file.delete()) {
                LOG.debug("Successfully deleted parent file " + file.toString());
            }
        }
    }


}


