	.text
			  # _main () (r)
	.p2align 4,0x90
	.globl _main
_main:
			  #  r = 1
	movl $1,%r10
	movl %r10d,(%rsp)
			  #  t1 = r
	movl r(%rip),%r10
	movl %r10d,4(%rsp)
			  #  t2 = t1 + r
	movl t1(%rip),%r10
	movl r(%rip),%r11
	addq %r11,%r10
	movl %eax,8(%rsp)
			  #  t3 = t2 + r
	movl t2(%rip),%r10
	movl r(%rip),%r11
	addq %r11,%r10
	movl %eax,12(%rsp)
			  #  t4 = t3 + r
	movl t3(%rip),%r10
	movl r(%rip),%r11
	addq %r11,%r10
	movl %eax,16(%rsp)
			  #  t5 = t4 + r
	movl t4(%rip),%r10
	movl r(%rip),%r11
	addq %r11,%r10
	movl %eax,20(%rsp)
			  #  t6 = t5 + r
	movl t5(%rip),%r10
	movl r(%rip),%r11
	addq %r11,%r10
	movl %eax,24(%rsp)
			  #  r = t5 + r
	movl t5(%rip),%r10
	movl r(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  call _printInt(r)
	movl r(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 33
