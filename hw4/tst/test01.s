	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  call _printInt(123)
	movl $123,%rdi
	call _printInt
			  #  call _printStr("abc")
	leaq _S0(%rip),%rdi
	call _printStr
			  #  call _printStr("second string")
	leaq _S1(%rip),%rdi
	call _printStr
			  #  call _printBool(true)
	movl $1,%rdi
	call _printBool
			  #  return 
_S0:
	.asciz "abc"
_S1:
	.asciz "second string"
			  # Total inst cnt: 11
