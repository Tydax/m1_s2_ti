CORPUS := corpus.md
TEMPLATE := mytemplate.latex
OUTPUT := university-report.pdf

report : $(CORPUS) $(METADATA)
	pandoc $(METADATA) 		\
	$(CORPUS)		 		\
	-f markdown				\
	--template=$(TEMPLATE)	\
	-o $(OUTPUT)			\
	-s
