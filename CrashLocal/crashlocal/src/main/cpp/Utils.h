//
// Created by darrenzeng on 2021/3/7.
//

#ifndef OPTIMIZE_DAY06_UTILS_H
#define OPTIMIZE_DAY06_UTILS_H
#include <signal.h>
#include <stdlib.h>

extern const char* desc_sig(int sig, int code);

extern const char* getProcessName(pid_t pid);

extern const char* getThreadName(pid_t tid);

extern bool is_dll(const char* dll_name);

#endif //OPTIMIZE_DAY06_UTILS_H
