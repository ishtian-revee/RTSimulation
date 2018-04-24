LOCAL_PATH := $(call my-dir)


#==================================================
# buils 3rd party lib
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := libjasper_static
LOCAL_MODULE_FILENAME := libjasper
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/liblibjasper.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := IlmImf_static
LOCAL_MODULE_FILENAME := IlmImf
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libIlmImf.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := tbb_static
LOCAL_MODULE_FILENAME := tbb
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libtbb.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libjpeg_static
LOCAL_MODULE_FILENAME := libjpeg
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/liblibjpeg.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libwebp_static
LOCAL_MODULE_FILENAME := libwebp
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/liblibwebp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpng_static
LOCAL_MODULE_FILENAME := libpng
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/liblibpng.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libtiff_static
LOCAL_MODULE_FILENAME := libtiff
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/liblibtiff.a
include $(PREBUILT_STATIC_LIBRARY)

#==================================================
# buils core
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := opencv_core_static
LOCAL_MODULE_FILENAME := libopencv_core
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libopencv_core.a
include $(PREBUILT_STATIC_LIBRARY)

#==================================================
# buils img codec
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := opencv_imgcodecs_static
LOCAL_MODULE_FILENAME := libopencv_imgcodecs
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libopencv_imgcodecs.a
LOCAL_STATIC_LIBRARIES := opencv_core_static
LOCAL_STATIC_LIBRARIES += opencv_imgproc_static
LOCAL_STATIC_LIBRARIES += libjasper_static
LOCAL_STATIC_LIBRARIES += IlmImf_static
LOCAL_STATIC_LIBRARIES += tbb_static
LOCAL_LDLIBS := z dl m log
include $(PREBUILT_STATIC_LIBRARY)

#==================================================
# buils img proc
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := opencv_imgproc_static
LOCAL_MODULE_FILENAME := libopencv_imgproc
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libopencv_imgproc.a
LOCAL_STATIC_LIBRARIES := opencv_core_static
LOCAL_STATIC_LIBRARIES += libjasper_static
LOCAL_STATIC_LIBRARIES += IlmImf_static
LOCAL_STATIC_LIBRARIES += tbb_static
LOCAL_LDLIBS := z dl m log
include $(PREBUILT_STATIC_LIBRARY)

#==================================================
# buils video io
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := opencv_videoio_static
LOCAL_MODULE_FILENAME := libopencv_videoio
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libopencv_videoio.a
LOCAL_STATIC_LIBRARIES := opencv_core_static
LOCAL_STATIC_LIBRARIES += opencv_imgcodecs_static
LOCAL_STATIC_LIBRARIES += libjasper_static
LOCAL_STATIC_LIBRARIES += IlmImf_static
LOCAL_STATIC_LIBRARIES += tbb_static
LOCAL_LDLIBS := z dl m log
include $(PREBUILT_STATIC_LIBRARY)

#==================================================
# buils photo
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := opencv_photo_static
LOCAL_MODULE_FILENAME := libopencv_photo
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libopencv_photo.a
LOCAL_STATIC_LIBRARIES := opencv_core_static
LOCAL_STATIC_LIBRARIES += opencv_imgcodecs_static
LOCAL_STATIC_LIBRARIES += libjasper_static
LOCAL_STATIC_LIBRARIES += IlmImf_static
LOCAL_STATIC_LIBRARIES += tbb_static
LOCAL_LDLIBS := z dl m log
include $(PREBUILT_STATIC_LIBRARY)

#==================================================
# buils high gui
#==================================================
include $(CLEAR_VARS)
LOCAL_MODULE := opencv_highgui_static
LOCAL_MODULE_FILENAME := libopencv_highgui
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libopencv_highgui.a
LOCAL_STATIC_LIBRARIES := opencv_core_static
LOCAL_STATIC_LIBRARIES += libjasper_static
LOCAL_STATIC_LIBRARIES += IlmImf_static
LOCAL_STATIC_LIBRARIES += tbb_static
LOCAL_LDLIBS := z dl m log
include $(PREBUILT_STATIC_LIBRARY)

LOCAL_CFLAGS += -fPIC -DANDROID -fsigned-char

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../include/opencv \
                           $(LOCAL_PATH)/../../include
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include/opencv \
                    $(LOCAL_PATH)/../../include
