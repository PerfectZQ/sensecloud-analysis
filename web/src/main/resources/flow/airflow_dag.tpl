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
{% endif %}
    catchup=False,
)

## Define operators
{% for task in flow.tasks %}
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


ck_host={{env.ck_host}}
ck_port={{env.ck_port}}
ck_user={{env.ck_user}}
ck_pwd={{env.ck_password}}

db={{ task.conf.db }}

{{task.taskId}}_cmd = r"""
  clickhouse-client \
      --host {ck_host} --port {ck_port} \
      --user {ck_user} --password {ck_pwd} \
      --database {db} \
      --multiquery \
      --query "{sql}"
""".format(sql={{task.taskId}}_sql, ck_host=ck_host, ck_port=ck_port, ck_user=ck_user, ck_pwd=ck_pwd, db=db)

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
{% for task in flow.tasks %}
{% for dependency in task.dependencyIds %}
{% if task.taskId != 'None' %}
op_{{task.taskId}}.set_upstream(op_{{dependency}})
{% endif %}
{% endfor %}
{% endfor %}