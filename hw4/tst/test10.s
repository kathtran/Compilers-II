	.text
			  # _go () 
	.p2align 4,0x90
	.globl _go
_go:
			  #  t1 = 1 < 2
	movl $1,%r10
	movl $2,%r11
	cmpq %r11,%r10
	setl %r10b
	movzbl %r10b,%r10d
	movl %eax,(%rsp)
			  #  if t1 == false goto L0
	movl t1(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L0
			  #  call _printInt(1)
	movl $1,%rdi
	call _printInt
			  #  goto L1
	jmp _go_L1
			  # L0:
_go_L0:
			  #  t2 = 3 * 4
	movl $3,%r10
	movl $4,%r11
	imulq %r11,%r10
	movl %eax,4(%rsp)
			  #  t3 = t2 == 10
	movl t2(%rip),%r10
	movl $10,%r11
	cmpq %r11,%r10
	sete %r10b
	movzbl %r10b,%r10d
	movl %eax,8(%rsp)
			  #  if t3 == false goto L2
	movl t3(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L2
			  #  call _printInt(4)
	movl $4,%rdi
	call _printInt
			  #  goto L3
	jmp _go_L3
			  # L2:
_go_L2:
			  #  call _printInt(5)
	movl $5,%rdi
	call _printInt
			  # L3:
_go_L3:
			  # L1:
_go_L1:
			  #  return 6
	movl $6,%eax
	addq $60,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t4 = call _go()
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t4)
	movl t4(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 44
