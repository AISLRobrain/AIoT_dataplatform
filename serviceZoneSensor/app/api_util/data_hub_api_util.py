import uuid

from api_util.http_request import http_get, http_post
from api_util.data_hub_dto.post_entity_dto import RadarDatasetDTO

def post_data_hub_sensing_cin(sensor_type, DATAHUB_URL_HOST_INGEST_ENDPOINT, each_datahub_train_path, SENSOR_ID, train_data, max_retries=10):
    short_uuid = str(uuid.uuid4())[:8]

    entity_ids = ["urn:" + short_uuid + ':' + SENSOR_ID]
    if sensor_type == "radar":
        dataset_dto = RadarDatasetDTO(each_datahub_train_path, entity_ids, train_data)
    else:
        print("Unknown sensor type")
        return None

    url = DATAHUB_URL_HOST_INGEST_ENDPOINT 
    headers = {
        "Content-Type": "application/json"
    }

    retries = 0
    while retries < max_retries:
        response_post = http_post(url, json=dataset_dto.to_dict(), headers=headers)
        
        if response_post is not None:
            return response_post
        else:
            retries += 1
            print(f"Retry {retries} for URL: {url}")

    print(f"Failed to post data after {max_retries} attempts for URL: {url}")
    return None
