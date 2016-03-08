	.text
			  # _value (i, j, k) 
	.p2align 4,0x90
	.globl _value
_value:
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
			  # _go () 
	.p2align 4,0x90
	.globl _go
_go:
			  #  t3 = call _value(1, 1, 1)
	movl $1,%rdi
	movl $1,%rsi
	movl $1,%rdx
	call _value
	movl %eax,(%rsp)
			  #  t4 = call _value(2, 2, 2)
	movl $2,%rdi
	movl $2,%rsi
	movl $2,%rdx
	call _value
	movl %eax,4(%rsp)
			  #  t5 = t3 + t4
	movl t3(%rip),%r10
	movl t4(%rip),%r11
	addq %r11,%r10
	movl %eax,8(%rsp)
			  #  return t5
	movl t5(%rip),%eax
	addq $24,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t6 = call _go()
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t6)
	movl t6(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 42
