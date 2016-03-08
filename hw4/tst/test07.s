	.text
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t1 = call _go()
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t1)
	movl t1(%rip),%rdi
	call _printInt
			  #  return 
			  # _go () (a, b)
	.p2align 4,0x90
	.globl _go
_go:
			  #  t2 = call _malloc(8)
	movl $8,%rdi
	call _malloc
	movl %eax,8(%rsp)
			  #  a = t2
	movl t2(%rip),%r10
	movl %r10d,(%rsp)
			  #  t3 = call _malloc(8)
	movl $8,%rdi
	call _malloc
	movl %eax,12(%rsp)
			  #  b = t3
	movl t3(%rip),%r10
	movl %r10d,4(%rsp)
			  #  t4 = 0 * 4
	movl $0,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,16(%rsp)
			  #  t5 = a + t4
	movl a(%rip),%r10
	movl t4(%rip),%r11
	addq %r11,%r10
	movl %eax,20(%rsp)
			  #  [t5] = 1
	movl $1,%r10
	movl t5(%rip),%r11
	movq %r10d,%r11
			  #  t6 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,24(%rsp)
			  #  t7 = a + t6
	movl a(%rip),%r10
	movl t6(%rip),%r11
	addq %r11,%r10
	movl %eax,28(%rsp)
			  #  [t7] = 2
	movl $2,%r10
	movl t7(%rip),%r11
	movq %r10d,%r11
			  #  t8 = 0 * 4
	movl $0,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,32(%rsp)
			  #  t9 = b + t8
	movl b(%rip),%r10
	movl t8(%rip),%r11
	addq %r11,%r10
	movl %eax,36(%rsp)
			  #  [t9] = 3
	movl $3,%r10
	movl t9(%rip),%r11
	movq %r10d,%r11
			  #  t10 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,40(%rsp)
			  #  t11 = b + t10
	movl b(%rip),%r10
	movl t10(%rip),%r11
	addq %r11,%r10
	movl %eax,44(%rsp)
			  #  [t11] = 4
	movl $4,%r10
	movl t11(%rip),%r11
	movq %r10d,%r11
			  #  t12 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,48(%rsp)
			  #  t13 = a + t12
	movl a(%rip),%r10
	movl t12(%rip),%r11
	addq %r11,%r10
	movl %eax,52(%rsp)
			  #  t14 = [t13]
	movl t13(%rip),%r10
	movq %r10,56(%rsp)
			  #  call _printInt(t14)
	movl t14(%rip),%rdi
	call _printInt
			  #  t15 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,60(%rsp)
			  #  t16 = b + t15
	movl b(%rip),%r10
	movl t15(%rip),%r11
	addq %r11,%r10
	movl %eax,64(%rsp)
			  #  t17 = [t16]
	movl t16(%rip),%r10
	movq %r10,68(%rsp)
			  #  call _printInt(t17)
	movl t17(%rip),%rdi
	call _printInt
			  #  t18 = 0 * 4
	movl $0,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,72(%rsp)
			  #  t19 = a + t18
	movl a(%rip),%r10
	movl t18(%rip),%r11
	addq %r11,%r10
	movl %eax,76(%rsp)
			  #  t20 = [t19]
	movl t19(%rip),%r10
	movq %r10,80(%rsp)
			  #  return t20
	movl t20(%rip),%eax
	addq $120,%rsp
	ret
			  # Total inst cnt: 100
