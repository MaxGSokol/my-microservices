package org.myproject.botcoreapp.infrastructure.voximplant;

import com.voximplant.apiclient.ClientException;
import com.voximplant.apiclient.VoximplantAPIClient;
import com.voximplant.apiclient.request.StartScenariosRequest;
import com.voximplant.apiclient.response.StartScenariosResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class StartScenario {
    public static void executeCall(String telNumber, String text) throws ClientException {
        try {
            VoximplantAPIClient client = new VoximplantAPIClient(
                    "src/main/resources/8486f811-e46b-40e4-8915-6dca2ea24401_private.json"
            );
            String customData = telNumber + "#VOX#SEP#" + URLEncoder.encode(text, StandardCharsets.UTF_8);
            StartScenariosResponse response = client.startScenarios(
                    new StartScenariosRequest().setRuleId(3907594).setScriptCustomData(customData)
            );
            log.info(customData);
            System.out.println("Ok" + response);
        } catch (IOException | ClientException e) {
            e.printStackTrace();
        }

    }
}
