	.text
			  # _foo (i) (x)
	.p2align 4,0x90
	.globl _foo
_foo:
	movl %r9d,(%rsp)
			  #  return i
	movl i(%rip),%eax
	addq $12,%rsp
	ret
			  # _bar (i) (x)
	.p2align 4,0x90
	.globl _bar
_bar:
	movl %r9d,(%rsp)
			  #  x = 2
	movl $2,%r10
	movl %r10d,4(%rsp)
			  #  return x
	movl x(%rip),%eax
	addq $24,%rsp
	ret
			  # _main () (i, j)
	.p2align 4,0x90
	.globl _main
_main:
			  #  t1 = call _foo(1)
	movl $1,%rdi
	call _foo
	movl %eax,8(%rsp)
			  #  i = t1
	movl t1(%rip),%r10
	movl %r10d,(%rsp)
			  #  t2 = call _bar(1)
	movl $1,%rdi
	call _bar
	movl %eax,12(%rsp)
			  #  j = t2
	movl t2(%rip),%r10
	movl %r10d,4(%rsp)
			  #  call _printInt(i)
	movl i(%rip),%rdi
	call _printInt
			  #  call _printInt(j)
	movl j(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 31
