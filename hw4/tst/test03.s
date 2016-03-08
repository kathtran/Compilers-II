	.text
			  # _main () (i, b)
	.p2align 4,0x90
	.globl _main
_main:
			  #  t1 = 2 * 4
	movl $2,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,8(%rsp)
			  #  t2 = 2 + t1
	movl $2,%r10
	movl t1(%rip),%r11
	addq %r11,%r10
	movl %eax,12(%rsp)
			  #  t3 = 9 / 3
	movl $9,%rax
	movl $3,%r10
	cqto %rax,%rdx
	idivq %r10,%rax
	movl %eax,16(%rsp)
			  #  t4 = t2 - t3
	movl t2(%rip),%r10
	movl t3(%rip),%r11
	subq %r11,%r10
	movl %eax,20(%rsp)
			  #  i = t4
	movl t4(%rip),%r10
	movl %r10d,(%rsp)
			  #  t5 = true
	movl $1,%r10
	movl %r10d,24(%rsp)
			  #  t6 = 1 > 2
	movl $1,%r10
	movl $2,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %eax,28(%rsp)
			  #  if t6 == true goto L0
	movl t6(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _main_L0
			  #  t7 = false
	movl $0,%r10
	movl %r10d,32(%rsp)
			  #  t8 = 3 < 4
	movl $3,%r10
	movl $4,%r11
	cmpq %r11,%r10
	setl %r10b
	movzbl %r10b,%r10d
	movl %eax,36(%rsp)
			  #  if t8 == false goto L1
	movl t8(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _main_L1
			  #  t9 = !false
	movl $0,%r10
	notq %r10,40(%rsp)
	movl %r10,40(%rsp)
			  #  if t9 == false goto L1
	movl t9(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _main_L1
			  #  t7 = true
	movl $1,%r10
	movl %r10d,32(%rsp)
			  # L1:
_main_L1:
			  #  if t7 == true goto L0
	movl t7(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _main_L0
			  #  t5 = false
	movl $0,%r10
	movl %r10d,24(%rsp)
			  # L0:
_main_L0:
			  #  b = t5
	movl t5(%rip),%r10
	movl %r10d,4(%rsp)
			  #  call _printInt(i)
	movl i(%rip),%rdi
	call _printInt
			  #  call _printBool(b)
	movl b(%rip),%rdi
	call _printBool
			  #  return 
			  # Total inst cnt: 67
