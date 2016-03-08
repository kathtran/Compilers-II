	.text
			  # _go (n) (i)
	.p2align 4,0x90
	.globl _go
_go:
	movl %r9d,(%rsp)
			  #  i = 0
	movl $0,%r10
	movl %r10d,4(%rsp)
			  #  t1 = n > 0
	movl n(%rip),%r10
	movl $0,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %eax,8(%rsp)
			  #  if t1 == false goto L0
	movl t1(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L0
			  #  call _printInt(n)
	movl n(%rip),%rdi
	call _printInt
			  #  t2 = n - 1
	movl n(%rip),%r10
	movl $1,%r11
	subq %r11,%r10
	movl %eax,12(%rsp)
			  #  t3 = call _back(t2)
	movl t2(%rip),%rdi
	call _back
	movl %eax,16(%rsp)
			  #  i = t3
	movl t3(%rip),%r10
	movl %r10d,4(%rsp)
			  # L0:
_go_L0:
			  #  return i
	movl i(%rip),%eax
	addq $44,%rsp
	ret
			  # _back (n) (i)
	.p2align 4,0x90
	.globl _back
_back:
	movl %r9d,(%rsp)
			  #  t4 = call _go(n)
	movl n(%rip),%rdi
	call _go
	movl %eax,8(%rsp)
			  #  i = t4
	movl t4(%rip),%r10
	movl %r10d,4(%rsp)
			  #  return 0
	movl $0,%eax
	addq $20,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t5 = call _go(5)
	movl $5,%rdi
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t5)
	movl t5(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 48
