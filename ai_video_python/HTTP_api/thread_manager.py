import threading
import random
import string
from video_processor import process_video_stream

threads = {}
thread_data = {}

def generate_random_name():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=8))

def start_thread(rtsp_urls, model_paths, taskId):
    name = generate_random_name()
    stop_event = threading.Event()
    thread = threading.Thread(target=process_video_stream, args=(name, rtsp_urls, model_paths, stop_event, taskId))
    threads[name] = {"thread": thread, "stop_event": stop_event}
    thread_data[name] = {"rtsp_urls": rtsp_urls, "model_paths": model_paths}
    thread.start()

    return name

def stop_thread(name):
    if name in threads:
        thread_info = threads[name]
        stop_event = thread_info["stop_event"]
        stop_event.set()
        thread_info["thread"].join()
        threads.pop(name)
        thread_data.pop(name)
        return True
    else:
        return False
