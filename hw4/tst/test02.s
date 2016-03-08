	.text
			  # _main () (b, i, j)
	.p2align 4,0x90
	.globl _main
_main:
			  #  b = true
	movl $1,%r10
	movl %r10d,(%rsp)
			  #  i = 2
	movl $2,%r10
	movl %r10d,4(%rsp)
			  #  j = 6
	movl $6,%r10
	movl %r10d,8(%rsp)
			  #  call _printBool(b)
	movl b(%rip),%rdi
	call _printBool
			  #  call _printInt(i)
	movl i(%rip),%rdi
	call _printInt
			  #  call _printInt(j)
	movl j(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 15
