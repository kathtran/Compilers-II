	.text
			  # _go () 
	.p2align 4,0x90
	.globl _go
_go:
			  #  t1 = call _value(true)
	movl $1,%rdi
	call _value
	movl %eax,(%rsp)
			  #  return t1
	movl t1(%rip),%eax
	addq $8,%rsp
	ret
			  # _value (cond) (i, j, k)
	.p2align 4,0x90
	.globl _value
_value:
	movl %r9d,(%rsp)
			  #  i = 5
	movl $5,%r10
	movl %r10d,4(%rsp)
			  #  j = 6
	movl $6,%r10
	movl %r10d,8(%rsp)
			  #  if cond == false goto L0
	movl cond(%rip),%r10
	movl $0,%r11
	cmpq %r11d,%r10d
	je _value_L0
			  #  k = i
	movl i(%rip),%r10
	movl %r10d,12(%rsp)
			  #  goto L1
	jmp _value_L1
			  # L0:
_value_L0:
			  #  k = j
	movl j(%rip),%r10
	movl %r10d,12(%rsp)
			  # L1:
_value_L1:
			  #  return k
	movl k(%rip),%eax
	addq $52,%rsp
	ret
			  # _main () 
	.p2align 4,0x90
	.globl _main
_main:
			  #  t2 = call _go()
	call _go
	movl %eax,(%rsp)
			  #  call _printInt(t2)
	movl t2(%rip),%rdi
	call _printInt
			  #  return 
			  # Total inst cnt: 34
