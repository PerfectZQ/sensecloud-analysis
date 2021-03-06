package sensecloud.submitter.remote;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Data
@Component
public class KubernetesClient {

    @Autowired
    private KubernetesClientConf conf;

    private ApiClient client;

    private CoreV1Api apiV1;

    public KubernetesClient() {
        try {
            if(conf != null) {
                ClientBuilder builder = new ClientBuilder();
                String apiServer = conf.getKubernetes_api_server();
                if(StringUtils.isNotBlank(apiServer)) {
                    builder.setBasePath(conf.getKubernetes_api_server());
                    if(apiServer.toLowerCase().contains("https")) {
                        builder.setVerifyingSsl(true);
                    }
                }

                String token = conf.getKubernetes_oauth_token();
                if(StringUtils.isNotBlank(token)) {
                    builder.setAuthentication(new AccessTokenAuthentication(token));
                }

                this.client = builder.build();
            } else {
                this.client = Config.defaultClient();
            }
            Configuration.setDefaultApiClient(client);
            this.apiV1 = new CoreV1Api();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public V1Pod getPod (String namespace, String podName) {
        V1Pod pod = null;
        try {
            log.info(">>>>> Get Pod: namespace = " + namespace + ", pod = " + podName);
            pod = apiV1.readNamespacedPod(podName, namespace, "true", false, false);
        } catch ( ApiException e) {
            e.printStackTrace();
        }
        return pod;
    }

    public boolean stopPod(String namespace, String podName) {
        try {
            V1Pod pod = this.getPod(namespace, podName);
            if(pod != null) {
                pod = apiV1.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null);
                log.info("Stop and delete pod {} in namespace {} successfully", podName, namespace);
            } else {
                log.warn(">>>>>>>>>>>>>>>> Pod Not Found namespace = {}, pod = {}", namespace, podName);
            }
            return true;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createSecret(String namespace, String secretName, Map<String, byte[]> originalSecretData) {
        V1Secret v1Secret = new V1Secret();

        Map<String, byte[]> encodedSecretData = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : originalSecretData.entrySet()) {
            encodedSecretData.put(entry.getKey(), Base64.getEncoder().encode(entry.getValue()));
        }

        v1Secret.setData(encodedSecretData);
        v1Secret.setApiVersion("v1");
        v1Secret.setKind("Secret");
        v1Secret.setType("Opaque");

        V1ObjectMeta meta = new V1ObjectMeta();
        meta.setName(secretName);
        meta.setNamespace(namespace);
        meta.setSelfLink("/api/v1/namespaces/" + namespace + "/secrets/" + secretName);
        meta.setUid(UUID.randomUUID().toString());
        v1Secret.setMetadata(meta);
        try {
            System.out.println(v1Secret);
            apiV1.createNamespacedSecret(namespace, v1Secret, null, null, null);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public V1SecretList listSecrets(String namespace) {
        try {
            V1SecretList list = apiV1.listNamespacedSecret(namespace, null, false, null, null, null, 0, null, 300 * 1000, null);
            return list;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public V1Secret getSecret(String namespace, String name) {
        try {
            V1Secret secret = apiV1.readNamespacedSecret(name, namespace, null, null, null);
            return secret;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException {
        ApiClient client = new ClientBuilder().setBasePath("https://mordor.sensetime.com/k8s/clusters/c-t5mbq").setVerifyingSsl(true)
                .setAuthentication(new AccessTokenAuthentication("kubeconfig-u-4dchth6r7v:r4pczsx4f74m6hgb5tm7tw5w4d5vl2rmpkfnsst62l25wndzzg9652")).build();

        Configuration.setDefaultApiClient(client);

//        Configuration.setDefaultApiClient(Config.defaultClient());
        CoreV1Api apiV1 = new CoreV1Api();
        V1Pod pod = null;
        try {
//            V1PodList list = apiV1.listNamespacedPod("dlink-prod", null, false, null, null, null, 10, null, 30, false);
//            list.getItems().forEach(v1Pod -> {
//                System.out.println(v1Pod.getMetadata().getName());
//                System.out.println("==========================================");
//            });

            pod = apiV1.readNamespacedPod("connector-kafka-prv-1", "sensecloud-data-viz", "true", false, false);
            System.out.println(pod);
            System.out.println("==========================================");
            pod = apiV1.deleteNamespacedPod("connector-kafka-prv-1", "sensecloud-data-viz", null, null, null, null, null, null);
            System.out.println(pod);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

}
