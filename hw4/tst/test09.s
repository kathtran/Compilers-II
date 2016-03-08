	.text
			  # _go () (b, i, a)
	.p2align 4,0x90
	.globl _go
_go:
			  #  i = 0
	movl $0,%r10
	movl %r10d,4(%rsp)
			  #  t1 = call _malloc(16)
	movl $16,%rdi
	call _malloc
	movl %eax,12(%rsp)
			  #  a = t1
	movl t1(%rip),%r10
	movl %r10d,8(%rsp)
			  #  t2 = 1 * 4
	movl $1,%r10
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
			  #  t4 = true
	movl $1,%r10
	movl %r10d,24(%rsp)
			  #  t5 = true
	movl $1,%r10
	movl %r10d,28(%rsp)
			  #  t6 = 1 < 2
	movl $1,%r10
	movl $2,%r11
	cmpq %r11,%r10
	setl %r10b
	movzbl %r10b,%r10d
	movl %eax,32(%rsp)
			  #  if t6 == true goto L1
	movl t6(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _go_L1
			  #  t7 = false
	movl $0,%r10
	movl %r10d,36(%rsp)
			  #  t8 = 3 > 4
	movl $3,%r10
	movl $4,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %eax,40(%rsp)
			  #  if t8 == false goto L2
	movl t8(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L2
			  #  t9 = 7 * 8
	movl $7,%r10
	movl $8,%r11
	imulq %r11,%r10
	movl %eax,44(%rsp)
			  #  t10 = 6 + t9
	movl $6,%r10
	movl t9(%rip),%r11
	addq %r11,%r10
	movl %eax,48(%rsp)
			  #  t11 = 5 == t10
	movl $5,%r10
	movl t10(%rip),%r11
	cmpq %r11,%r10
	sete %r10b
	movzbl %r10b,%r10d
	movl %eax,52(%rsp)
			  #  if t11 == false goto L2
	movl t11(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L2
			  #  t7 = true
	movl $1,%r10
	movl %r10d,36(%rsp)
			  # L2:
_go_L2:
			  #  if t7 == true goto L1
	movl t7(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _go_L1
			  #  t5 = false
	movl $0,%r10
	movl %r10d,28(%rsp)
			  # L1:
_go_L1:
			  #  if t5 == true goto L0
	movl t5(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _go_L0
			  #  t12 = !true
	movl $1,%r10
	notq %r10,56(%rsp)
	movl %r10,56(%rsp)
			  #  if t12 == true goto L0
	movl t12(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _go_L0
			  #  t4 = false
	movl $0,%r10
	movl %r10d,24(%rsp)
			  # L0:
_go_L0:
			  #  b = t4
	movl t4(%rip),%r10
	movl %r10d,(%rsp)
			  #  t13 = -3
	movl $3,%r10
	negq %r10,60(%rsp)
	movl %r10,60(%rsp)
			  #  t14 = -t13
	movl t13(%rip),%r10
	negq %r10,64(%rsp)
	movl %r10,64(%rsp)
			  #  t15 = 5 * 4
	movl $5,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,68(%rsp)
			  #  t16 = t15 / 2
	movl t15(%rip),%rax
	movl $2,%r10
	cqto %rax,%rdx
	idivq %r10,%rax
	movl %eax,72(%rsp)
			  #  t17 = 1 * 4
	movl $1,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,76(%rsp)
			  #  t18 = a + t17
	movl a(%rip),%r10
	movl t17(%rip),%r11
	addq %r11,%r10
	movl %eax,80(%rsp)
			  #  t19 = [t18]
	movl t18(%rip),%r10
	movq %r10,84(%rsp)
			  #  t20 = t16 * t19
	movl t16(%rip),%r10
	movl t19(%rip),%r11
	imulq %r11,%r10
	movl %eax,88(%rsp)
			  #  t21 = t14 + t20
	movl t14(%rip),%r10
	movl t20(%rip),%r11
	addq %r11,%r10
	movl %eax,92(%rsp)
			  #  t22 = i * 2
	movl i(%rip),%r10
	movl $2,%r11
	imulq %r11,%r10
	movl %eax,96(%rsp)
			  #  t23 = t21 + t22
	movl t21(%rip),%r10
	movl t22(%rip),%r11
	addq %r11,%r10
	movl %eax,100(%rsp)
			  #  i = t23
	movl t23(%rip),%r10
	movl %r10d,4(%rsp)
			  #  call _printBool(b)
	movl b(%rip),%rdi
	call _printBool
			  #  return i
	movl i(%rip),%eax
	addq $180,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t24 = call _go()
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t24)
	movl t24(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 142
