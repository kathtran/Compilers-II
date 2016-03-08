	.text
			  # _go (i, j, k) 
	.p2align 4,0x90
	.globl _go
_go:
	movl %r9d,(%rsp)
	movl %r8d,4(%rsp)
	movl %edx,8(%rsp)
			  #  t1 = i + j
	movl i(%rip),%r10
	movl j(%rip),%r11
	addq %r11,%r10
	movl %eax,12(%rsp)
			  #  t2 = t1 + k
	movl t1(%rip),%r10
	movl k(%rip),%r11
	addq %r11,%r10
	movl %eax,16(%rsp)
			  #  return t2
	movl t2(%rip),%eax
	addq $24,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t3 = call _go(1, 2, 3)
	movl $1,%rdi
	movl $2,%rsi
	movl $3,%rdx
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t3)
	movl t3(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 26
