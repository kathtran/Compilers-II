	.file	"example2.c"
	.text
	.globl	_f
	.def	_f;	.scl	2;	.type	32;	.endef
_f:
LFB7:
	.cfi_startproc
	movl	4(%esp), %edx
	movl	8(%esp), %eax
	leal	(%edx,%eax), %ecx
	imull	%eax, %ecx
	leal	(%ecx,%edx,2), %edx
	imull	%edx, %eax
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
