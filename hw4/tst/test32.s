	.text
			  # _foo (i) 
	.p2align 4,0x90
	.globl _foo
_foo:
	movl %r9d,(%rsp)
			  #  t1 = i > 1
	movl i(%rip),%r10
	movl $1,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %eax,4(%rsp)
			  #  if t1 == false goto L0
	movl t1(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _foo_L0
			  #  t2 = call _bar()
	call _bar
	movl %eax,8(%rsp)
			  #  return t2
	movl t2(%rip),%eax
	addq $36,%rsp
	ret
			  #  goto L1
	jmp _foo_L1
			  # L0:
_foo_L0:
			  #  return 3
	movl $3,%eax
	addq $36,%rsp
	ret
			  # L1:
_foo_L1:
			  # _bar () 
	.p2align 4,0x90
	.globl _bar
_bar:
			  #  t3 = call _foo(1)
	movl $1,%rdi
	call _foo
	movl %eax,(%rsp)
			  #  return t3
	movl t3(%rip),%eax
	addq $8,%rsp
	ret
			  # _main () (i)
	.p2align 4,0x90
	.globl _main
_main:
			  #  t4 = call _foo(2)
	movl $2,%rdi
	call _foo
	movl %eax,4(%rsp)
			  #  i = t4
	movl t4(%rip),%r10
	movl %r10d,(%rsp)
			  #  call _printInt(i)
	movl i(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 40
