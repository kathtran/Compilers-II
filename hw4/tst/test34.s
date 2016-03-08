	.text
			  # _foo (i) (k)
	.p2align 4,0x90
	.globl _foo
_foo:
	movl %r9d,(%rsp)
			  #  k = 10
	movl $10,%r10
	movl %r10d,4(%rsp)
			  #  t1 = i > 0
	movl i(%rip),%r10
	movl $0,%r11
	cmpq %r11,%r10
	setg %r10b
	movzbl %r10b,%r10d
	movl %eax,8(%rsp)
			  #  if t1 == false goto L0
	movl t1(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _foo_L0
			  #  t2 = call _bar(i)
	movl i(%rip),%rdi
	call _bar
	movl %eax,12(%rsp)
			  #  t3 = call _foo(t2)
	movl t2(%rip),%rdi
	call _foo
	movl %eax,16(%rsp)
			  #  t4 = k + t3
	movl k(%rip),%r10
	movl t3(%rip),%r11
	addq %r11,%r10
	movl %eax,20(%rsp)
			  #  k = t4
	movl t4(%rip),%r10
	movl %r10d,4(%rsp)
			  # L0:
_foo_L0:
			  #  return k
	movl k(%rip),%eax
	addq $44,%rsp
	ret
			  # _bar (i) 
	.p2align 4,0x90
	.globl _bar
_bar:
	movl %r9d,(%rsp)
			  #  t5 = i - 1
	movl i(%rip),%r10
	movl $1,%r11
	subq %r11,%r10
	movl %eax,4(%rsp)
			  #  return t5
	movl t5(%rip),%eax
	addq $12,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t6 = call _foo(2)
	movl $2,%rdi
	call _foo
	movl %eax,(%rsp)
			  #  call _printInt(t6)
	movl t6(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 48
