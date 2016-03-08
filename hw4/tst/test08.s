	.text
			  # _main () (a, b, i)
	.p2align 4,0x90
	.globl _main
_main:
			  #  t1 = call _malloc(8)
	movl $8,%rdi
	call _malloc
	movl %eax,12(%rsp)
			  #  a = t1
	movl t1(%rip),%r10
	movl %r10d,(%rsp)
			  #  t2 = 0 * 4
	movl $0,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,16(%rsp)
			  #  t3 = a + t2
	movl a(%rip),%r10
	movl t2(%rip),%r11
	addq %r11,%r10
	movl %eax,20(%rsp)
			  #  [t3] = 2
	movl $2,%r10
	movl t3(%rip),%r11
	movq %r10d,%r11
			  #  t4 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,24(%rsp)
			  #  t5 = a + t4
	movl a(%rip),%r10
	movl t4(%rip),%r11
	addq %r11,%r10
	movl %eax,28(%rsp)
			  #  [t5] = 4
	movl $4,%r10
	movl t5(%rip),%r11
	movq %r10d,%r11
			  #  i = 0
	movl $0,%r10
	movl %r10d,8(%rsp)
			  #  t6 = i * 4
	movl i(%rip),%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,32(%rsp)
			  #  t7 = a + t6
	movl a(%rip),%r10
	movl t6(%rip),%r11
	addq %r11,%r10
	movl %eax,36(%rsp)
			  #  t8 = [t7]
	movl t7(%rip),%r10
	movq %r10,40(%rsp)
			  #  t9 = i + 1
	movl i(%rip),%r10
	movl $1,%r11
	addq %r11,%r10
	movl %eax,44(%rsp)
			  #  t10 = t9 * 4
	movl t9(%rip),%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,48(%rsp)
			  #  t11 = a + t10
	movl a(%rip),%r10
	movl t10(%rip),%r11
	addq %r11,%r10
	movl %eax,52(%rsp)
			  #  t12 = [t11]
	movl t11(%rip),%r10
	movq %r10,56(%rsp)
			  #  t13 = t8 + t12
	movl t8(%rip),%r10
	movl t12(%rip),%r11
	addq %r11,%r10
	movl %eax,60(%rsp)
			  #  b = t13
	movl t13(%rip),%r10
	movl %r10d,4(%rsp)
			  #  call _printInt(b)
	movl b(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 64
