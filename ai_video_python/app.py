# app.py
from flask import Flask
from HTTP_api.routes import setup_routes

app = Flask(__name__)
setup_routes(app)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5050)