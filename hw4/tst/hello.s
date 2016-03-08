	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  call _printStr("Hello World!")
	leaq _S0(%rip),%rdi
	call _printStr
			  #  return 
_S0:
	.asciz "Hello World!"
			  # Total inst cnt: 5
