# -*- coding: utf-8 -*-
from datetime import timedelta
from airflow import DAG
from airflow.contrib.operators.kubernetes_pod_operator import KubernetesPodOperator
from airflow.utils.dates import days_ago

from base64 import b64encode, b64decode

app_name = "{{ appName }}"
kubernetes_context = "{{ env.kubernetes_context }}"
kubernetes_namespace = "{{ env.kubernetes_namespace }}"
kubernetes_project = "{{ env.kubernetes_project }}"

job_config = """
{{ config | raw }}

"""

oauthToken = "{{ env.kubernetes_oauth_token }}"

# These args will get passed on to each operator
# You can override them on a per-task basis during operator initialization

default_args = {
    'owner': '{{ runner.username }}',
    'depends_on_past': False,
    'start_date': days_ago(2),
    'email': ['{{ runner.email }}'],
    'email_on_failure': True,
    'email_on_retry': True,
    'retries': 2147483647,
    'retry_delay': timedelta(minutes=1),
}

dag = DAG(
    dag_id=app_name,
    default_args=default_args,
    description='SENSECLOUD-ANALYSIS data connect job: kafka -> ClickHouse',
    # https://airflow.apache.org/docs/stable/dag-run.html
    # schedule_interval='@once',
    schedule_interval=None,
    catchup=False
)

affinity = {
    'nodeAffinity': {
        'preferredDuringSchedulingIgnoredDuringExecution': [
            {
                "weight": 100,
                "preference": {
                    "matchExpressions": [
                        {
                            "key": "project",
                            "operator": "In",
                            "values": [kubernetes_project]
                        }
                    ]
                }
            }
        ]
    }
}

tolerations = [
    {
        'effect': 'NoExecute',
        'key': "project",
        'operator': 'Equal',
        'value': kubernetes_project
    }
]

resources = {
    "limit_cpu": "100m",
    "limit_memory": "256Mi",
    "request_cpu": "100m",
    "request_memory": "256Mi"
}

registry = "registry.sensetime.com"
group = "plat-bigdata"
app = "kafka2clickhouse"
tag = "latest"
image = "%s/%s/%s:%s" % (registry, group, app, tag)

base64_bytes = b64encode(bytes(job_config, encoding="utf-8"))
base64_bytes_str = bytes.decode(base64_bytes, encoding="utf-8")
b64decode_bytes = b64decode(base64_bytes)
b64decode_str = bytes.decode(b64decode_bytes, encoding="utf-8")

env_vars = {
    "JOB_CONFIG": base64_bytes_str
}

submit = [
    "sh", "-c",
    r"""
        /opt/spark/bin/spark-submit \
        --master "{{ env.kubernetes_api_server }}" \
        --deploy-mode cluster \
        --name "{app_name}" \
        --driver-cores 1 --driver-memory 1g \
        --class "com.sensetime.dlink.streaming.Kafka2ClickHouse" \
        --files "local:///app/conf/kafka.truststore.jks,local:///app/conf/job.config" \
        --conf "spark.executor.instances=1" \
        --conf "spark.kubernetes.executor.limit.cores=1" \
        --conf "spark.kubernetes.executor.limit.memory=2Gi" \
        --conf "spark.jars.ivy=/tmp/.ivy" \
        --conf "spark.kubernetes.container.image={image}" \
        --conf "spark.kubernetes.container.image.pullPolicy=IfNotPresent" \
        --conf "spark.kubernetes.container.image.pullSecrets=sensetime" \
        --conf "spark.kubernetes.context={{ env.kubernetes_context }}" \
        --conf "spark.kubernetes.namespace={{ env.kubernetes_namespace }}" \
        --conf "spark.kubernetes.authenticate.driver.serviceAccountName=dlink-spark" \
        --conf "spark.kubernetes.authenticate.submission.oauthToken={oauthToken}" \
        --conf "spark.streaming.concurrentJobs=1" \
        --conf "spark.streaming.backpressure.enabled=true" \
        --conf "spark.streaming.kafka.maxRatePerPartition=10000" \
        --conf "spark.streaming.kafka.maxRetries=3" \
        --conf "spark.streaming.kafka.consumer.poll.ms=310000" \
        --conf "spark.kubernetes.driver.pod.name=connector-{app_name}" \
        "local:///app/app.jar" \
        --jobConfig "$JOB_CONFIG" \
    """.format(app_name=app_name, image=image, oauthToken=oauthToken)
]

spark_submit_operator = KubernetesPodOperator(
    name=app_name + "-submitter",
    task_id="spark-streaming-submit",
    in_cluster=False,
    cluster_context=kubernetes_context,
    config_file='/opt/bitnami/airflow/.kube/config',
    namespace=kubernetes_namespace,
    image=image,
    image_pull_policy="Always",
    image_pull_secrets="sensetime",
    service_account_name='default',
    arguments=submit,
    labels={"app": "spark-submit"},
    startup_timeout_seconds=60 * 60,
    # ports=[port],
    # volumes=[volume],
    depends_on_past=False,
    # volume_mounts=[volume_mount],
    affinity=affinity,
    env_vars=env_vars,
    is_delete_operator_pod=True,
    hostnetwork=False,
    tolerations=tolerations,
    resources=resources,
    # configmaps=configmaps,
    dag=dag,
    get_logs=True
)
