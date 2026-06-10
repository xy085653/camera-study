ARG PYTHON_IMAGE=python:3.12-slim
FROM $PYTHON_IMAGE

WORKDIR /app

# 国内加速（阿里云 PyPI 镜像）
COPY requirements.txt .
RUN pip install --no-cache-dir -i https://mirrors.aliyun.com/pypi/simple/ -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["gunicorn", "config.wsgi:application", "--bind", "0.0.0.0:8000", "--workers", "4"]
