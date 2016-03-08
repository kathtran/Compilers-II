	.text
			  # _go () (a, b, c, x)
	.p2align 4,0x90
	.globl _go
_go:
			  #  a = true
	movl $1,%r10
	movl %r10d,(%rsp)
			  #  t1 = !a
	movl a(%rip),%r10
	notq %r10,16(%rsp)
	movl %r10,16(%rsp)
			  #  b = t1
	movl t1(%rip),%r10
	movl %r10d,4(%rsp)
			  #  t2 = true
	movl $1,%r10
	movl %r10d,20(%rsp)
			  #  t3 = false
	movl $0,%r10
	movl %r10d,24(%rsp)
			  #  if a == false goto L1
	movl a(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L1
			  #  if b == false goto L1
	movl b(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L1
			  #  t3 = true
	movl $1,%r10
	movl %r10d,24(%rsp)
			  # L1:
_go_L1:
			  #  if t3 == true goto L0
	movl t3(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _go_L0
			  #  if a == true goto L0
	movl a(%rip),%r10
	movl $1,%r11
	cmpq %r11d,%r10d
	je _go_L0
			  #  t2 = false
	movl $0,%r10
	movl %r10d,20(%rsp)
			  # L0:
_go_L0:
			  #  c = t2
	movl t2(%rip),%r10
	movl %r10d,8(%rsp)
			  #  if c == false goto L2
	movl c(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _go_L2
			  #  x = 1
	movl $1,%r10
	movl %r10d,12(%rsp)
			  #  goto L3
	jmp _go_L3
			  # L2:
_go_L2:
			  #  x = 0
	movl $0,%r10
	movl %r10d,12(%rsp)
			  # L3:
_go_L3:
			  #  return x
	movl x(%rip),%eax
	addq $100,%rsp
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
			  # Total inst cnt: 54
