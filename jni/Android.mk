# Android ndk makefile for DSdroid

LOCAL_BUILD_PATH := $(call my-dir)

include $(CLEAR_VARS)

include $(LOCAL_BUILD_PATH)/cpudetect/cpudetect.mk

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
include $(LOCAL_BUILD_PATH)/desmume_neon.mk
include $(LOCAL_BUILD_PATH)/desmume_v7.mk
else
include $(LOCAL_BUILD_PATH)/desmume_compat.mk
endif

include $(LOCAL_BUILD_PATH)/desmume/src/android/agg/agg_compat.mk
include $(LOCAL_BUILD_PATH)/desmume/src/android/7z/7z.mk
