# 카프카 메시지 예시
{
    "eventId": null,
    "eventTransId": null,
    "fromSystemName": null,
    "toSystemName": null,
    "contentType": null,
    "messageType": "run",
    "content": {
        "sensorParam": {
            "Sensor2": {
                "range": 2.0,
                "profile": 5.0
            },
            "Sensor3": {
                "range": 5.0,
                "profile": 5.0
            },
            "Sensor1": {
                "range": 2.0,
                "profile": 6.0
            }
        },
        "filterParam": {
            "nginx": {
                "threshold": 62.57273284203146
            }
        },
        "hyperParam": {
            "nginx": {
                "learningRate": 0.000649490259572541,
                "epoch": 68.0,
                "batchSize": 26.0
            }
        },
        "sensorType": "radar",
        "sensorTargets": {
            'Sensor1': 'metal',
            'Sensor2': 'glass',
            'Sensor3': 'plastic'
        },
        "dataResourceInformation": {
            "ioTPlatformInformation": {
                "statePath": [
                    "/Mobius/service244/Sensor1/state/service244Sensor1state",
                    "/Mobius/service244/Sensor2/state/service244Sensor2state",
                    "/Mobius/service244/Sensor3/state/service244Sensor3state"
                ],
                "targetPath": [
                    "/Mobius/service244/Sensor1/target/service244Sensor1",
                    "/Mobius/service244/Sensor2/target/service244Sensor2",
                    "/Mobius/service244/Sensor3/target/service244Sensor3"
                ],
                "trainDataPath": [
                    "/Mobius/service244/Sensor1/train/e8d9996f",
                    "/Mobius/service244/Sensor2/train/e8d9996f",
                    "/Mobius/service244/Sensor3/train/e8d9996f"
                ],
                "testDataPath": [
                    "/Mobius/service244/Sensor1/test/e8d9996f",
                    "/Mobius/service244/Sensor2/test/e8d9996f",
                    "/Mobius/service244/Sensor3/test/e8d9996f"
                ]
            },
            "dataHubInformation": {
                "rawTrainDataPath": [
                    "service244Sensor1traine8d9996f",
                    "service244Sensor2traine8d9996f",
                    "service244Sensor3traine8d9996f"
                ],
                "rawTestDataPath": [
                    "service244Sensor1teste8d9996f",
                    "service244Sensor2teste8d9996f",
                    "service244Sensor3teste8d9996f"
                ],
                "filterTrainDataPath": [
                    "service244Sensor1train9df16b11",
                    "service244Sensor2train9df16b11",
                    "service244Sensor3train9df16b11"
                ],
                "filterTestDataPath": [
                    "service244Sensor1test9df16b11",
                    "service244Sensor2test9df16b11",
                    "service244Sensor3test9df16b11"
                ]
            }
        },
        "sensingStopCondition": {
            "stopCondition": "count",
            "stopConditionValue": 1000
        }
    }
}