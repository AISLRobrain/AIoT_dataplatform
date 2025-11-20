class RadarDatasetDTO:
    def __init__(self, dataset_id, entity_ids, train_data):
        self.datasetId = dataset_id
        self.entities = [{"id": entity_id, 
                          "type": "ArrayDouble",
                          "@context": [
                "http://uri.citydatahub.kr/ngsi-ld/v1/context.jsonld"
            ],
            "refParkingSpots": {
                "type": "Property",
                "value": train_data
            }
            } for entity_id in entity_ids]


    def to_dict(self):
        return {
            "datasetId": self.datasetId,
            "entities": self.entities
        }
