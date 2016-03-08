	.text
			  # _main () (r)
	.p2align 4,0x90
	.globl _main
_main:
			  #  t1 = 1
	movl $1,%r10
	movl %r10d,4(%rsp)
			  #  t2 = 2
	movl $2,%r10
	movl %r10d,8(%rsp)
			  #  t3 = 3
	movl $3,%r10
	movl %r10d,12(%rsp)
			  #  t4 = call _f(t1, t2, t3)
	movl t1(%rip),%rdi
	movl t2(%rip),%rsi
	movl t3(%rip),%rdx
	call _f
	movl %eax,16(%rsp)
			  #  call _printInt(t4)
	movl t4(%rip),%rdi
	call _printInt
			  #  return 
			  # _f (a, b, c) 
	.p2align 4,0x90
	.globl _f
_f:
	movl %r9d,(%rsp)
	movl %r8d,4(%rsp)
	movl %edx,8(%rsp)
			  #  t1 = call _g(a, b, c)
	movl a(%rip),%rdi
	movl b(%rip),%rsi
	movl c(%rip),%rdx
	call _g
	movl %eax,12(%rsp)
			  #  t2 = call _g(b, c, a)
	movl b(%rip),%rdi
	movl c(%rip),%rsi
	movl a(%rip),%rdx
	call _g
	movl %eax,16(%rsp)
			  #  t3 = t2 - t1
	movl t2(%rip),%r10
	movl t1(%rip),%r11
	subq %r11,%r10
	movl %eax,20(%rsp)
			  #  return t3
	movl t3(%rip),%eax
	addq $28,%rsp
	ret
			  # _g (x, y, z) 
	.p2align 4,0x90
	.globl _g
_g:
	movl %r9d,(%rsp)
	movl %r8d,4(%rsp)
	movl %edx,8(%rsp)
			  #  t1 = z + y
	movl z(%rip),%r10
	movl y(%rip),%r11
	addq %r11,%r10
	movl %eax,12(%rsp)
			  #  t2 = t1 - x
	movl t1(%rip),%r10
	movl x(%rip),%r11
	subq %r11,%r10
	movl %eax,16(%rsp)
			  #  return t2
	movl t2(%rip),%eax
	addq $24,%rsp
	ret
			  # Total inst cnt: 54
