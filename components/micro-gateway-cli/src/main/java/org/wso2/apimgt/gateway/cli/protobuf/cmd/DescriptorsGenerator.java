package org.wso2.apimgt.gateway.cli.protobuf.cmd;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.ExtensionRegistry;
import org.wso2.apimgt.gateway.cli.protobuf.ProtobufConstants;
import org.wso2.apimgt.gateway.cli.exception.GrpcCodeGenException;
import org.wso2.apimgt.gateway.cli.protobuf.utils.ProtocCommandBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;

/**
 * Class for generate file descriptors for proto files.
 */
public class DescriptorsGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(DescriptorsGenerator.class);

    public static DescriptorProtos.FileDescriptorProto generateRootFileDescriptor (String exePath, String protoPath,
                                                                               String descriptorPath) {
        String command = new ProtocCommandBuilder
                (exePath, protoPath, resolveProtoFolderPath(protoPath), descriptorPath).build();
        generateDescriptor(command);
        File initialFile = new File (descriptorPath);
        try (InputStream targetStream = new FileInputStream(initialFile)) {
            ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
            //todo: add elements to the extensionRegistry
            DescriptorProtos.FileDescriptorSet set = DescriptorProtos.FileDescriptorSet.parseFrom(targetStream,
                    extensionRegistry);
            if (set.getFileList().size() > 0) {
                return set.getFile(0);
            }
        } catch (IOException e) {
            throw new GrpcCodeGenException("Error reading generated descriptor file '" + descriptorPath + "'.", e);
        }
        return null;
    }

    /**
     * Execute command and generate file descriptor.
     *
     * @param command protoc executor command.
     */
    public static void generateDescriptor(String command) {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase(Locale.ENGLISH).startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new GrpcCodeGenException("Error in executing protoc command '" + command + "'.", e);
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new GrpcCodeGenException("Process not successfully completed. Process is interrupted while" +
                    " running the protoc executor.", e);
        }
        if (process.exitValue() != 0) {
            try (BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(process.getErrorStream(), "UTF-8"))) {
                String err;
                StringBuilder errMsg = new StringBuilder();
                while ((err = bufferedReader.readLine()) != null) {
                    errMsg.append(System.lineSeparator()).append(err);
                }
                throw new GrpcCodeGenException(errMsg.toString());
            } catch (IOException e) {
                throw new GrpcCodeGenException("Invalid command syntax.", e);
            }
        }
    }

    /**
     * Resolve proto folder path from Proto file path.
     *
     * @param protoPath Proto file path
     * @return Parent folder path of proto file.
     */
    public static String resolveProtoFolderPath(String protoPath) {
        int idx = protoPath.lastIndexOf(ProtobufConstants.FILE_SEPARATOR);
        String protoFolderPath = ProtobufConstants.EMPTY_STRING;
        if (idx > 0) {
            protoFolderPath = protoPath.substring(0, idx);
        }
        return protoFolderPath;
    }
}
