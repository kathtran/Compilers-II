	.text
			  # _go () (b)
	.p2align 4,0x90
	.globl _go
_go:
			  #  t1 = call _malloc(8)
	movl $8,%rdi
	call _malloc
	movl %eax,4(%rsp)
			  #  b = t1
	movl t1(%rip),%r10
	movl %r10d,(%rsp)
			  #  t2 = 0 * 4
	movl $0,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,8(%rsp)
			  #  t3 = b + t2
	movl b(%rip),%r10
	movl t2(%rip),%r11
	addq %r11,%r10
	movl %eax,12(%rsp)
			  #  [t3] = 3
	movl $3,%r10
	movl t3(%rip),%r11
	movq %r10d,%r11
			  #  t4 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,16(%rsp)
			  #  t5 = b + t4
	movl b(%rip),%r10
	movl t4(%rip),%r11
	addq %r11,%r10
	movl %eax,20(%rsp)
			  #  [t5] = 4
	movl $4,%r10
	movl t5(%rip),%r11
	movq %r10d,%r11
			  #  t6 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,24(%rsp)
			  #  t7 = b + t6
	movl b(%rip),%r10
	movl t6(%rip),%r11
	addq %r11,%r10
	movl %eax,28(%rsp)
			  #  t8 = [t7]
	movl t7(%rip),%r10
	movq %r10,32(%rsp)
			  #  call _printInt(t8)
	movl t8(%rip),%rdi
	call _printInt
			  #  t9 = 0 * 4
	movl $0,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,36(%rsp)
			  #  t10 = b + t9
	movl b(%rip),%r10
	movl t9(%rip),%r11
	addq %r11,%r10
	movl %eax,40(%rsp)
			  #  t11 = [t10]
	movl t10(%rip),%r10
	movq %r10,44(%rsp)
			  #  return t11
	movl t11(%rip),%eax
	addq $68,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t12 = call _go()
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t12)
	movl t12(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 61
