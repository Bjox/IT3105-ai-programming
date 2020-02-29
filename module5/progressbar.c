#include <Python.h>
#include <stdio.h>
#include "progressbar.h"

static int last = -1;

static PyObject* progressbar(PyObject* self, PyObject* args)
{
    int length;
    int current;
    int max;

    if (!PyArg_ParseTuple(args,"iii", &length, &current, &max))
       return NULL;

    float percent = current / (float)max;
    int intpercent = (int)(percent * 100.0f);

    if (intpercent == last)
        return Py_BuildValue("");

    last = intpercent;
    int pos = length * percent;
    printf("\r[");
    for (size_t i = 0; i < length; i++) {
        if (i < pos)
            printf("=");
        else
            printf(" ");
    }
    printf("] %.0f%%", percent*100);
    fflush(stdout);
    return Py_BuildValue("");
}


static PyMethodDef mod_methods[] = {
    {"progressbar", progressbar, METH_VARARGS, "progressbar..."},
    {NULL, NULL, 0, NULL}
};


static struct PyModuleDef progmod =
{
    PyModuleDef_HEAD_INIT,
    "progmod",
    "",
    -1,
    mod_methods
};


PyMODINIT_FUNC PyInit_progressbar(void)
{
    return PyModule_Create(&progmod);
}
