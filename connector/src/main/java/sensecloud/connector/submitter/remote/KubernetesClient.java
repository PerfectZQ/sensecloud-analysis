package sensecloud.connector.submitter.remote;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.util.Config;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Component
public class KubernetesClient {

    private ApiClient client;

    private CoreV1Api apiV1;

    public KubernetesClient() {
        try {
            this.client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            this.apiV1 = new CoreV1Api();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


}
