d<-read.csv("k1", header=TRUE, sep=";")

pca<-prcomp(d[,1:1024], scale=FALSE)

eig<-pca$sdev*pca$sdev

var<-eig/sum(eig)*100

pcs<-cumsum(var)

print(var[1:3])

plot(pca$x[,1], pca$x[,2], col="blue")

paste(pca$x[,1], pca$x[,2], pca$x[,3])
