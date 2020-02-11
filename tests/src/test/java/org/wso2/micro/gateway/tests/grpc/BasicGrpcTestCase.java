package org.wso2.micro.gateway.tests.grpc;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.wso2.micro.gateway.tests.common.BaseTestCase;
import org.wso2.micro.gateway.tests.common.model.ApplicationDTO;
import org.wso2.micro.gateway.tests.util.TestConstant;
import org.wso2.micro.gateway.tests.util.TokenUtil;

public class BasicGrpcTestCase extends BaseTestCase {
    private String jwtTokenProd;
    @BeforeClass
    public void start() throws Exception {
        String project = "OpenApiThrottlingProject";
        //Define application info
        ApplicationDTO application = new ApplicationDTO();
        application.setName("jwtApp");
        application.setTier("Unlimited");
        application.setId((int) (Math.random() * 1000));

        jwtTokenProd = TokenUtil.getBasicJWT(application, new JSONObject(),
                TestConstant.KEY_TYPE_PRODUCTION, 3600);
        //generate apis with CLI and start the micro gateway server
        super.init(project, new String[]{"common_api.yaml"});
    }

}
