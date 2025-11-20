import requests
import hashlib
import json
import ast
import numpy as np

def http_get(url, params=None, headers=None, iotPlatform=None):

    if iotPlatform == True:
        headers = {
            'Accept': 'application/json',
            'X-M2M-RI': '12345',
            'X-M2M-Origin': 'SOrigin'
        }

    try:
        response = requests.get(url, params=params, headers=headers, timeout=10)  # timeout 추가
        response.raise_for_status()
        return json.loads(response.text)

    except requests.ConnectTimeout:
        # 연결 시간 초과 예외 처리
        print(f"Connection timed out for URL: {url}")
        return None

    except requests.HTTPError as http_err:
        # HTTP 오류 응답 예외 처리
        print(f"HTTP error occurred for URL {url}: {http_err}")
        return None

    except Exception as err:
        # 그 외의 예외 처리
        print(f"An error occurred for URL {url}: {err}")
        return None
    


def http_post(url, data=None, json=None, headers=None, iotPlatform=None, ty=None):

    if iotPlatform == True:
        headers = {
            'Accept': 'application/json',
            'X-M2M-RI': '12345',
            'X-M2M-Origin': '{{aei}}',
            'Content-Type': 'application/vnd.onem2m-res+json; ty={}'.format(ty)
        }

    try:
        response = requests.post(url, data=data, json=json, headers=headers, timeout=10)  # timeout 추가
        response.raise_for_status()
        return response.text

    except requests.ConnectTimeout:
        # 연결 시간 초과 예외 처리
        print(f"Connection timed out for URL: {url}")
        return None

    except requests.HTTPError as http_err:
        # HTTP 오류 응답 예외 처리
        print(f"HTTP error occurred for URL {url}: {http_err}")
        return None

    except Exception as err:
        # 그 외의 예외 처리
        print(f"An error occurred for URL {url}: {err}")
        return None





if __name__ == "__main__":
    # # 사용 예시
    # # GET 요청 예시
    # response_get = http_get("https://jsonplaceholder.typicode.com/posts/1")
    # print(response_get.json())

    # # POST 요청 예시
    # data_to_post = {"title": "foo", "body": "bar", "userId": 1}
    # response_post = http_post("https://jsonplaceholder.typicode.com/posts", json=data_to_post)
    # print(response_post.json())


    arr = np.array(["urn:123:[12312,3123]", "urn:1412412:123123:[123213, 412231, 12321]"])

    # print(extract_entity_id(arr))

    # for x in extract_entity_id_array(arr):
        # print(x)