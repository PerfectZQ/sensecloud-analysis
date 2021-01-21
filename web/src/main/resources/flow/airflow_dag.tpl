# -*- coding: utf-8 -*-
from datetime import timedelta
from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from airflow.utils.dates import days_ago

app_name = "{{ flow.name }}"

# These args will get passed on to each operator
# You can override them on a per-task basis during operator initialization
default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': days_ago(2),
    'email': ['zhangqiang@sensetime.com'],
    'email_on_failure': True,
    'email_on_retry': True,
    'retries': 0,
    'retry_delay': timedelta(minutes=1),
}

{% if env is not empty %}
{% for entry in env %}
{{ entry.key }} = {{ entry.value }}
{% endfor %}
{% endif %}

dag = DAG(
    dag_id=app_name,
    default_args=default_args,
    description=app_name,
{% if flow.scheduleExpr is empty %}
    schedule_interval=None,
{% else %}
    schedule_interval={{ flow.scheduleExpr }}
{% %}
    catchup=False,
)

## Define operators
{% for task in tasks %}
{% if task.type == 'CLICKHOUSE_SQL' and task.content is not empty %}
## Define operator for task {{task.taskId}}
{{task.taskId}}_sql = """
CREATE TABLE IF NOT EXISTS {{ task.conf.db }}.{{ task.conf.table }}_shard
ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard_cat}/{{ task.conf.db }}.{{ task.conf.table }}_shard', '{replica_cat}')
ORDER BY tuple()
ON CLUSTER cat AS {{ task.content }};

CREATE TABLE IF NOT EXISTS {{ task.conf.db }}.{{ task.conf.table }} ON CLUSTER cat AS {{ task.conf.db }}.{{ task.conf.table }}_shard
ENGINE = Distributed('cat', '{{ task.conf.db }}', '{{ task.conf.table }}_shard', rand());
""".replace("`", "").replace("\r\n", " ").replace("\r", " ").replace("\n", " ")

{{task.taskId}}_cmd = rf"""
  clickhouse-client \
      --host {{env.ck_host}} --port {{env.ck_port}} \
      --user {{env.ck_user}} --password {{env.ck_password}} \
      --database {{ task.conf.db }} \
      --multiquery \
      --query "{{{task.taskId}}_sql}"
"""
op_{{task.taskId}} = BashOperator(
    task_id='{{task.taskId}}',
    depends_on_past=False,
    bash_command={{task.taskId}}_cmd,
    retries=0,
    dag=dag
)

{% endif %}
{% endfor %}

## Define dependencies
{% for task in tasks %}
{% for dependency in task.dependencyIds %}
op_{{task.taskId}}.set_upstream(op_{{dependency}})
{% endfor %}
{% endfor %}