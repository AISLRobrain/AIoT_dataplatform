import multiprocessing
from api_util.iot_platform_api_util import sub_iot_platform_cin_con

def sensor_waiting_mprocess(process_count, nu,port):
    pool = multiprocessing.Pool(processes=process_count)
    result = pool.starmap(sub_iot_platform_cin_con, [(nu,port)])
    pool.close()
    pool.join()
        
    if pool == "ready":
        return result