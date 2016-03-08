	.text
			  # _go () (i, j)
	.p2align 4,0x90
	.globl _go
_go:
			  #  i = 4
	movl $4,%r10
	movl %r10d,(%rsp)
			  #  t1 = i + 2
	movl i(%rip),%r10
	movl $2,%r11
	addq %r11,%r10
	movl %eax,8(%rsp)
			  #  j = t1
	movl t1(%rip),%r10
	movl %r10d,4(%rsp)
			  #  return j
	movl j(%rip),%eax
	addq $24,%rsp
	ret
			  # _main () (r)
	.p2align 4,0x90
	.globl _main
_main:
			  #  t2 = call _go()
	call _go
	movl %eax,4(%rsp)
			  #  r = t2
	movl t2(%rip),%r10
	movl %r10d,(%rsp)
			  #  call _printInt(r)
	movl r(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 22
