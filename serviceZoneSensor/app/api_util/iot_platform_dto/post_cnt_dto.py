class M2MPostCntDTO:
    def __init__(self, rn, lbl):
        self.m2m_cnt = {
            "m2m:cnt": {
                "rn": rn,
                "lbl": lbl,
                "mbs": 16834
            }
        }