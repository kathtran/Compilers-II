	.text
			  # _foo (i, j) 
	.p2align 4,0x90
	.globl _foo
_foo:
	movl %r9d,(%rsp)
	movl %r8d,4(%rsp)
			  #  t1 = i + j
	movl i(%rip),%r10
	movl j(%rip),%r11
	addq %r11,%r10
	movl %eax,8(%rsp)
			  #  return t1
	movl t1(%rip),%eax
	addq $24,%rsp
	ret
			  # _main () (b, i, j)
	.p2align 4,0x90
	.globl _main
_main:
			  #  b = true
	movl $1,%r10
	movl %r10d,(%rsp)
			  #  t2 = call _foo(1, 2)
	movl $1,%rdi
	movl $2,%rsi
	call _foo
	movl %eax,12(%rsp)
			  #  i = t2
	movl t2(%rip),%r10
	movl %r10d,4(%rsp)
			  #  t3 = 2 * 3
	movl $2,%r10
	movl $3,%r11
	imulq %r11,%r10
	movl %eax,16(%rsp)
			  #  j = t3
	movl t3(%rip),%r10
	movl %r10d,8(%rsp)
			  #  call _printBool(b)
	movl b(%rip),%rdi
	call _printBool
			  #  call _printInt(i)
	movl i(%rip),%rdi
	call _printInt
			  #  call _printInt(j)
	movl j(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 34
