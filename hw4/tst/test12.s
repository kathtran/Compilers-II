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
			  #  t4 = 4
	movl $4,%r10
	movl %r10d,16(%rsp)
			  #  t5 = 5
	movl $5,%r10
	movl %r10d,20(%rsp)
			  #  t6 = 6
	movl $6,%r10
	movl %r10d,24(%rsp)
			  #  t7 = 7
	movl $7,%r10
	movl %r10d,28(%rsp)
			  #  t8 = 8
	movl $8,%r10
	movl %r10d,32(%rsp)
			  #  t9 = 9
	movl $9,%r10
	movl %r10d,36(%rsp)
			  #  t10 = 10
	movl $10,%r10
	movl %r10d,40(%rsp)
			  #  t11 = 11
	movl $11,%r10
	movl %r10d,44(%rsp)
			  #  t12 = 12
	movl $12,%r10
	movl %r10d,48(%rsp)
			  #  r = 0
	movl $0,%r10
	movl %r10d,(%rsp)
			  #  r = r + t12
	movl r(%rip),%r10
	movl t12(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t11
	movl r(%rip),%r10
	movl t11(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t10
	movl r(%rip),%r10
	movl t10(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t9
	movl r(%rip),%r10
	movl t9(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t8
	movl r(%rip),%r10
	movl t8(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t7
	movl r(%rip),%r10
	movl t7(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t6
	movl r(%rip),%r10
	movl t6(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t5
	movl r(%rip),%r10
	movl t5(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t4
	movl r(%rip),%r10
	movl t4(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t3
	movl r(%rip),%r10
	movl t3(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t2
	movl r(%rip),%r10
	movl t2(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  r = r + t1
	movl r(%rip),%r10
	movl t1(%rip),%r11
	addq %r11,%r10
	movl %eax,(%rsp)
			  #  call _printInt(r)
	movl r(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 79
