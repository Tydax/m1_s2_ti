CORPUS := corpus.md
TEMPLATE := mytemplate.latex
OUTPUT := university-report
METADATA := metadata.yaml

latex : $(CORPUS) $(METADATA)
	pandoc $(METADATA) 		\
	$(CORPUS)		 		\
	-f markdown				\
	--template=$(TEMPLATE)	\
	-o $(OUTPUT).tex			\
	-s
pdf : $(CORPUS) $(METADATA)
	pandoc $(METADATA) 		\
	$(CORPUS)		 		\
	-f markdown				\
	--template=$(TEMPLATE)	\
	-o $(OUTPUT).pdf			\
	-s
