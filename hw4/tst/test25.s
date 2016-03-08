	.text
			  # _go () 
	.p2align 4,0x90
	.globl _go
_go:
			  #  t1 = call _value(1, 2, 3)
	movl $1,%rdi
	movl $2,%rsi
	movl $3,%rdx
	call _value
	movl %eax,(%rsp)
			  #  return t1
	movl t1(%rip),%eax
	addq $8,%rsp
	ret
			  # _value (i, j, k) 
	.p2align 4,0x90
	.globl _value
_value:
	movl %r9d,(%rsp)
	movl %r8d,4(%rsp)
	movl %edx,8(%rsp)
			  #  t2 = i + j
	movl i(%rip),%r10
	movl j(%rip),%r11
	addq %r11,%r10
	movl %eax,12(%rsp)
			  #  t3 = t2 + k
	movl t2(%rip),%r10
	movl k(%rip),%r11
	addq %r11,%r10
	movl %eax,16(%rsp)
			  #  return t3
	movl t3(%rip),%eax
	addq $24,%rsp
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
			  # Total inst cnt: 33
