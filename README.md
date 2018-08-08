# Extrator de grafos sociais de fóruns no Moodle

Essa aplicação tem o propósito de extrair grafos sociais (no formato .graphml) de uma base de dados com schema Moodle (https://docs.moodle.org/dev/Database_Schema)
em uma versão PostgreSQL (instalada localmente, para alterar verifique o arquivo Conector.java).

As arestas dos grafos são dadas por interações entre usuários (vértices) no fórum de uma dada disciplina. A partir de uma data inicial, são gerados
grafos semanais cumulativos da disciplina.

Autores:
Henrique Lemos dos Santos - hlsantos@inf.ufrgs.br
Cristian Cechinel - contato@cristiancechinel.pro.br
