	.file	"example3.c"
	.text
	.def	_g.2469;	.scl	3;	.type	32;	.endef
_g.2469:
LFB8:
	.cfi_startproc
	movl	%eax, %edx
	addl	(%ecx), %edx
	imull	4(%ecx), %edx
	subl	%eax, %edx
	subl	%eax, %edx
	movl	%edx, %eax
	ret
	.cfi_endproc
LFE8:
	.globl	_f
	.def	_f;	.scl	2;	.type	32;	.endef
_f:
LFB7:
	.cfi_startproc
	pushl	%ebx
	.cfi_def_cfa_offset 8
	.cfi_offset 3, -8
	subl	$16, %esp
	.cfi_def_cfa_offset 24
	movl	24(%esp), %edx
	movl	28(%esp), %eax
	movl	%edx, 8(%esp)
	movl	%eax, 12(%esp)
	addl	%edx, %eax
	leal	8(%esp), %ecx
	call	_g.2469
	movl	%eax, %ebx
	leal	8(%esp), %ecx
	movl	$0, %eax
	call	_g.2469
	addl	%ebx, %eax
	addl	$16, %esp
	.cfi_def_cfa_offset 8
	popl	%ebx
	.cfi_restore 3
	.cfi_def_cfa_offset 4
	ret
	.cfi_endproc
LFE7:
	.def	___main;	.scl	2;	.type	32;	.endef
	.section .rdata,"dr"
LC0:
	.ascii "%d\12\0"
	.text
	.globl	_main
	.def	_main;	.scl	2;	.type	32;	.endef
_main:
LFB10:
	.cfi_startproc
	pushl	%ebp
	.cfi_def_cfa_offset 8
	.cfi_offset 5, -8
	movl	%esp, %ebp
	.cfi_def_cfa_register 5
	andl	$-16, %esp
	subl	$16, %esp
	call	___main
	movl	$2, 4(%esp)
	movl	$1, (%esp)
	call	_f
	movl	%eax, 4(%esp)
	movl	$LC0, (%esp)
	call	_printf
	leave
	.cfi_restore 5
	.cfi_def_cfa 4, 4
	ret
	.cfi_endproc
LFE10:
	.ident	"GCC: (GNU) 4.8.3"
	.def	_printf;	.scl	2;	.type	32;	.endef
