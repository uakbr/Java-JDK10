/* DO NOT EDIT THIS FILE - it is machine generated */
#include <StubPreamble.h>

/* Stubs for class InputFile */
/* SYMBOL: "InputFile/open()Z", Java_InputFile_open_stub */
stack_item *Java_InputFile_open_stub(stack_item *_P_,struct execenv *_EE_) {
	extern long InputFile_open(void *);
	_P_[0].i = (InputFile_open(_P_[0].p) ? TRUE : FALSE);
	return _P_ + 1;
}
/* SYMBOL: "InputFile/close()V", Java_InputFile_close_stub */
stack_item *Java_InputFile_close_stub(stack_item *_P_,struct execenv *_EE_) {
	extern void InputFile_close(void *);
	(void) InputFile_close(_P_[0].p);
	return _P_;
}
/* SYMBOL: "InputFile/read([BI)I", Java_InputFile_read_stub */
stack_item *Java_InputFile_read_stub(stack_item *_P_,struct execenv *_EE_) {
	extern long InputFile_read(void *,void *,long);
	_P_[0].i = InputFile_read(_P_[0].p,((_P_[1].p)),((_P_[2].i)));
	return _P_ + 1;
}