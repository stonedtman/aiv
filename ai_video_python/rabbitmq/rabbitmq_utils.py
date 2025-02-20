import pika
import json
from utils.yamlConfig import read_config

config = read_config()


def send_to_rabbitmq(queue_name, message):
    rabbitmq_config = config["rabbitmq"]
    credentials = pika.PlainCredentials(
        rabbitmq_config["username"], rabbitmq_config["password"]
    )
    parameters = pika.ConnectionParameters(
        host=rabbitmq_config["host"],
        port=rabbitmq_config["port"],
        credentials=credentials,
    )

    try:
        connection = pika.BlockingConnection(parameters)
        channel = connection.channel()
        channel.queue_declare(queue=queue_name, durable=True)
        channel.basic_publish(
            exchange="", routing_key=queue_name, body=json.dumps(message)
        )
        print(f"发送 '{json.dumps(message)}'")
        connection.close()
    except Exception as e:
        print(f"无法连接到RabbitMQ: {e}")
