;; Emacs lisp mode for editing java files.
;; $Id: java-mode.el,v 1.2 1995/04/06 03:03:21 mchapman Exp $  Mitch Chapman
;;
;; java-mode.el
;; ------------
;; Major mode for editing Java programs.
;; java-mode is an extension of c++-mode, and it uses alot of variables
;; from that mode.
;;
;; Author:  Mitch Chapman
;;

;; Maintainer: none
;; Keywords: languages, major modes

;; This file is *NOT* part of GNU Emacs.

;; java-mode.el is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; java-mode.el is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with java-mode.el; see the file COPYING.  If not, write to
;; the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

;;; Commentary:

;; This module derives an editing mode from c++-mode.  The derived mode
;; is for editing Java code.  (In the first release of this mode there's
;; very little specialized code; mostly you get a new mode-hook variable
;; ('java-mode-hook) and a new name ("Java") for your emacs mode line.

;; To use java-mode, put this in your .emacs file:
;; (autoload 'java-mode "yourLispCodeDirectory/java-mode" "java mode" t nil)
;; (setq auto-mode-alist
;;       (append '(("\\.java$" . java-mode)) auto-mode-alist))
;;

;;; Change Log:
;;    $Log: java-mode.el,v $
; Revision 1.2  1995/04/06  03:03:21  mchapman
; o (fix provided by Eric Mumpower, nocturne@mit.edu)
;   Original java-mode failed if c++-mode-hook not set.
; o Added java-indent-command for better indenting of Java methods.
;   Still needs lots of work.
;
; Revision 1.1  1995/04/06  02:50:28  mchapman
; Initial RCS revision

;;; Code:

(provide 'java-mode)

(defvar java-mode-map ()
  "Keymap used in Java mode.")

(defun java-mode ()
  "Major mode for editing java code.
See the documentation for c++-mode:  java-mode is an extension of
c++-mode.
Use the hook java-mode-hook to execute custom code when entering Java
mode.
\\{java-mode-map}"
  (interactive)
  (let ((current-c++-mode-hook (and (boundp 'c++-mode-hook) c++-mode-hook)))
    ;; Temporarily disable the c++-mode hook; don't wanna run
    ;; it when loading up c++-mode.
    (setq c++-mode-hook nil)

    (c++-mode)

    ;; Now customize c++-mode to give us any behaviors specific to
    ;; java mode.  (Hm; not much there right now...)
   
    ;; The Java mode map is the C++ mode map, but with a different name.
    (setq java-mode-map c++-mode-map)
    (use-local-map java-mode-map)
    (define-key java-mode-map "\t" 'java-indent-command)

    (setq major-mode 'java-mode
	  mode-name "Java")

    ;; Customize defun-matching expression to allow Java
    ;; functions to be identified.  This is tricky, and all I'm trying to
    ;; do is make it possible to put opening braces at the end of the
    ;; function signature definition, as in the distributed Java code.
    ;; I'm assuming that any line which starts with exactly one level of
    ;; indentation (four spaces in my case; adjust it for your style)
    ;; immediately followed by a letter, matches the beginning of a function
    ;; definition.
    ;; This won't match member data definitions because the function
    ;; "beginning-of-defun-raw" (see lisp.el) insists that a paragraph
    ;; start expression ends with some sort of open character (e.g. "{").
    (set (make-local-variable 'defun-prompt-regexp) "^    \\sw.*")

    ;; Restore the original c++-mode-hook.
    (setq c++-mode-hook current-c++-mode-hook)
    (run-hooks 'java-mode-hook)))


;;
;; These functions are lifted almost verbatim from cplus-md.el of
;; GNU emacs 19.28.
;; 
(defun java-indent-command (&optional whole-exp)
  "Indent current line as Java code, or in some cases insert a tab character.
If `c-tab-always-indent' is non-nil (the default), always indent current
line.  Otherwise, indent the current line only if point is at the left
margin or in the line's indentation; otherwise insert a tab.

A numeric argument, regardless of its value, means indent rigidly all means
indent rigidly all the lines of the expression starting after point so that
this line becomes properly indented.  The relative indentation among the
lines of the expression are preserved."
  (interactive "P")
  (if whole-exp
      ;; If arg, always indent this line as C
      ;; and shift remaining lines of expression the same amount.
      (let ((shift-amt (java-indent-line))
	    beg end)
	(save-excursion
	  (if c-tab-always-indent
	      (beginning-of-line))
	  (setq beg (point))
	  (forward-sexp 1)
	  (setq end (point))
	  (goto-char beg)
	  (forward-line 1)
	  (setq beg (point)))
	(if (> end beg)
	    (indent-code-rigidly beg end shift-amt "#")))
    (if (and (not c-tab-always-indent)
	     (save-excursion
	       (skip-chars-backward " \t")
	       (not (bolp))))
	(insert-tab)
      (java-indent-line))))

(defun java-indent-line ()
  "Indent current line as Java code.
Return the amount the indentation changed by."
  (let ((indent (calculate-java-indent nil))
	beg shift-amt
	(case-fold-search nil)
	(pos (- (point-max) (point))))
    (beginning-of-line)
    (setq beg (point))
    (cond ((eq indent nil)
	   (setq indent (current-indentation)))
	  ((eq indent t)
	   (setq indent (calculate-c-indent-within-comment)))
	  (t
	   (skip-chars-forward " \t")
	   (if (listp indent) (setq indent (car indent)))
	   (cond ((looking-at "default:")
		  (setq indent (+ indent c-label-offset)))
		 ((or (looking-at "case\\b")
		      (and (looking-at "[A-Za-z]")
			   (save-excursion
			     (forward-sexp 1)
			     (looking-at ":[^:]"))))
		  (setq indent (max 1 (+ indent c-label-offset))))
		 ((and (looking-at "else\\b")
		       (not (looking-at "else\\s_")))
		  (setq indent (save-excursion
				 (c-backward-to-start-of-if)
				 (current-indentation))))
		 ((= (following-char) ?})
		  (setq indent (- indent c-indent-level)))
		 ((= (following-char) ?{)
		  (setq indent (+ indent c-brace-offset))))))
    (skip-chars-forward " \t")
    (setq shift-amt (- indent (current-column)))
    (if (zerop shift-amt)
	(if (> (- (point-max) pos) (point))
	    (goto-char (- (point-max) pos)))
      (delete-region beg (point))
      (indent-to indent)
      ;; If initial point was within line's indentation,
      ;; position after the indentation.  Else stay at same point in text.
      (if (> (- (point-max) pos) (point))
	  (goto-char (- (point-max) pos))))
    shift-amt))

(defun calculate-java-indent (&optional parse-start)
  "Return appropriate indentation for current line as Java code.
In usual case returns an integer: the column to indent to.
Returns nil if line starts inside a string, t if in a comment."
  (save-excursion
    (beginning-of-line)
    (let ((indent-point (point))
	  (case-fold-search nil)
	  state
	  containing-sexp)
      (if parse-start
	  (goto-char parse-start)
	(beginning-of-java-class))
      (while (< (point) indent-point)
	(setq parse-start (point))
	(setq state (parse-partial-sexp (point) indent-point 0))
	(setq containing-sexp (car (cdr state))))
      (cond ((or (nth 3 state) (nth 4 state))
	     ;; return nil or t if should not change this line
	     (nth 4 state))
	    ((null containing-sexp)
	     ;; Line is at top level.  May be class, data or method
	     ;; definition, or may be function argument declaration or
	     ;; member initialization.
	     ;; Indent like the previous top level line unless
	     ;; (1) the previous line ends in a closeparen without semicolon,
	     ;; in which case this line is the first argument declaration or
	     ;; member initialization, or
	     ;; (2) the previous line begins with a colon,
	     ;; in which case this is the second line of member inits.
	     ;; It is assumed that arg decls and member inits are not mixed.
	     (goto-char indent-point)
	     (skip-chars-forward " \t")
	     (if (= (following-char) ?{)
		 0   ; Unless it starts a method body
	       (c++-backward-to-noncomment (or parse-start (point-min)))
	       (if (= (preceding-char) ?\))
		   (progn		; first arg decl or member init
		     (goto-char indent-point)
		     (skip-chars-forward " \t")
		     (if (= (following-char) ?:)
			 c++-member-init-indent
		       c-argdecl-indent))
		 (if (= (preceding-char) ?\;)
		     (backward-char 1))
		 (if (= (preceding-char) ?})
		     0
		   (beginning-of-line)	; continued arg decls or member inits
		   (skip-chars-forward " \t")
		   (if (= (following-char) ?:)
		       (if c++-continued-member-init-offset
			   (+ (current-indentation)
			      c++-continued-member-init-offset)
			 (progn
			   (forward-char 1)
			   (skip-chars-forward " \t")
			   (current-column)))
		     (current-indentation)))
		 )))
	    ((/= (char-after containing-sexp) ?{)
	     ;; line is expression, not statement:
	     ;; indent to just after the surrounding open -- unless
	     ;; empty arg list, in which case we do what
	     ;; c++-empty-arglist-indent says to do.
	     (if (and c++-empty-arglist-indent
		      (or (null (nth 2 state))	;; indicates empty arg
						;; list.
			  ;; Use a heuristic: if the first
			  ;; non-whitespace following left paren on
			  ;; same line is not a comment,
			  ;; is not an empty arglist.
			  (save-excursion
			    (goto-char (1+ containing-sexp))
			    (not
			     (looking-at "\\( \\|\t\\)*[^/\n]")))))
		 (progn
		   (goto-char containing-sexp)
		   (beginning-of-line)
		   (skip-chars-forward " \t")
		   (goto-char (min (+ (point) c++-empty-arglist-indent)
				   (1+ containing-sexp)))
		   (current-column))
	       ;; In C-mode, we would always indent to one after the
	       ;; left paren.  Here, though, we may have an
	       ;; empty-arglist, so we'll indent to the min of that
	       ;; and the beginning of the first argument.
	       (goto-char (1+ containing-sexp))
	       (current-column)))
	    (t
	     ;; Statement.  Find previous non-comment character.
	     (goto-char indent-point)
	     (c++-backward-to-noncomment containing-sexp)
	     (if (not (memq (preceding-char) '(nil ?\, ?\; ?} ?: ?\{)))
		 ;; This line is continuation of preceding line's statement;
		 ;; indent  c-continued-statement-offset  more than the
		 ;; previous line of the statement.
		 (progn
		   (c-backward-to-start-of-continued-exp containing-sexp)
		   (+ c-continued-statement-offset (current-column)
                      (if (save-excursion (goto-char indent-point)
					  (skip-chars-forward " \t")
					  (eq (following-char) ?{))
			  c-continued-brace-offset 0)))
	       ;; This line starts a new statement.
	       ;; Position following last unclosed open.
	       (goto-char containing-sexp)
	       ;; Is line first statement after an open-brace?
	       (or
		 ;; If no, find that first statement and indent like it.
		 (save-excursion
		   (forward-char 1)
		   (while (progn (skip-chars-forward " \t\n")
				 (looking-at
				  (concat
				   "/\\*\\|//"
				   "\\|case[ \t]"
				   "\\|[a-zA-Z0-9_$]*:[^:]")))
		     ;; Skip over comments and labels following openbrace.
		     (cond ((= (following-char) ?\#)
			    (forward-line 1))
			   ((looking-at "/\\*")
			    (search-forward "*/" nil 'move))
			   ((looking-at "//")
			    (forward-line 1))
			   (t
			    (re-search-forward ":[^:]" nil 'move))))
		      ;; The first following code counts
		      ;; if it is before the line we want to indent.
		      (and (< (point) indent-point)
			   (current-column)))
		 ;; If no previous statement,
		 ;; indent it relative to line brace is on.
		 ;; For open brace in column zero, don't let statement
		 ;; start there too.  If c-indent-offset is zero,
		 ;; use c-brace-offset + c-continued-statement-offset instead.
		 ;; For open-braces not the first thing in a line,
		 ;; add in c-brace-imaginary-offset.
		 (+ (if (and (bolp) (zerop c-indent-level))
			(+ c-brace-offset c-continued-statement-offset)
		      c-indent-level)
		    ;; Move back over whitespace before the openbrace.
		    ;; If openbrace is not first nonwhite thing on the line,
		    ;; add the c-brace-imaginary-offset.
		    (progn (skip-chars-backward " \t")
			   (if (bolp) 0 c-brace-imaginary-offset))
		    ;; If the openbrace is preceded by a parenthesized exp,
		    ;; move to the beginning of that;
		    ;; possibly a different line
		    (progn
		      (if (eq (preceding-char) ?\))
			  (forward-sexp -1))
		      ;; Get initial indentation of the line we are on.
		      (current-indentation))))))))))

;; This is lifted from emacs 19.28's lisp.el\beginning-of-defun.
(defconst java-identifier-regexp "[A-Za-z][A-Za-z0-9_$]*"
  "Regular expression to match a Java identifier.  (Does anyone know
whether or not such identifiers can begin with '_' or '$'?)")

(defconst java-class-def-regexp
  (concat "^\\(" java-identifier-regexp "[ \t]+\\)*"
	  "class[ \t]*")
  "Regular expression to match a Java class definition.")

(defun beginning-of-java-class (&optional arg)
  "Move point to the beginning of a Java class definition line.
With argument, do it that many times.  Negative arg -N
means move forward to Nth following beginning of class.
Returns t unless search stops due to beginning or end of buffer."
  (interactive "p")
  (and (beginning-of-java-class-raw arg)
       (progn (beginning-of-line) t)))

(defun beginning-of-java-class-raw (&optional arg)
  "Move point to the character that starts a class definition.
This is identical to beginning-of-java-class, except that point does not move
to the beginning of the line."
  (interactive "p")
  (beginning-of-java-regexp java-class-def-regexp arg))

(defun beginning-of-java-regexp (rexpr &optional arg)
  "Move point to the beginning of a regular expression.
With argument, do it that many times.  Negative arg -N means move
forward to Nth following beginning of rexpr.
Returns t unless search stops due to beginning or end of buffer."
  (interactive "p")
  (and arg (< arg 0) (not (eobp)) (forward-char 1))
  (and (re-search-backward rexpr nil 'move (or arg 1))
       (progn (goto-char (1- (match-end 0)))) t))


(defun end-of-java-class (&optional arg)
  "Move point to the end of a Java class definition.
With argument, do it that many times."
  (interactive "p")
  (beginning-of-java-class (- (1+ arg)))
  (re-search-backward "^}" nil 'move))


(defun beginning-of-java-method ()
  "Goto the beginning of the enclosing Java method."
  (interactive)
  (let ((currpoint (point))
	(at-beginning nil))
    (while (and (not at-beginning)
		(re-search-backward 
		 (concat "^[ \t]*"
			 "\\(" java-identifier-regexp "[ \t\n]+\\)*"
			 "\\(" java-identifier-regexp "[ \t\n]*\\)(")
		 (point-min) t))
      (goto-char (match-beginning 2))
      (setq at-beginning 
	    (and (not (or (looking-at "if")
			  (looking-at "else")
			  (looking-at "switch")))
		 (save-excursion
		   (re-search-forward ")[ \t]*{" currpoint t)))))
    (if at-beginning
	(beginning-of-line))))



;;;
;;; Variables and functions for use with imenu -- lets you pop up a
;;; menu of functions defined in a Java module.
;;; 
;;; 
;; Regular expression to find Java functions
;; Okay, so this works only with ASCII...
(defun java-imenu--function-name-regexp ()
  (concat 
   "^"
   ;; Include the number of spaces which would lead a properly-
   ;; indented Java member function.  This is a bad way to do
   ;; business because it fails to find functions if things aren't
   ;; already properly indented.
   (make-string c-continued-statement-offset ? )
   "[a-zA-Z0-9:]+[ \t]?"		; type specs; there can be no
   "\\([a-zA-Z0-9_$]+[ \t]+\\)?"	; more than 3 tokens, right?
   "\\([a-zA-Z0-9_$]+[ \t]+\\)?"
   "\\([ \t]*\\)?"			; pointer
   "\\([a-zA-Z0-9_$]+\\)[ \t]*("	; name
   ))

(defun java-imenu--create-index ()
  (imenu-example--create-c-index (java-imenu--function-name-regexp)))
