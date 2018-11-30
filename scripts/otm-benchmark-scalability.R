load_scalability <- function(operation, baseDir) {
  providers <- c("alibaba", "empire", "jopa", "komma", "rdfbeans")
  heaps <- c("32m", "64m", "128m", "256m", "512m", "1g")
  scal <- list()
  for (provider in providers) {
    scal[[provider]] <- c()
    for (heap in heaps) {
      res <- performance_stats(baseDir, operation, provider, heap)
      scal[[provider]] <- c(scal[[provider]], res$mean)
    }
  }
  return(scal)
}

scalability_stats <- function(base_dir, operation, plot_title) {
  scalability <- load_scalability(operation, base_dir)
  g_range <- range(0, scalability$alibaba, scalability$empire, scalability$jopa, scalability$komma, scalability$rdfbeans, na.rm=TRUE)
  mar.default <- c(5,4,4,2) + 0.1
  par(mar = mar.default + c(0, 1, 0, 0)) 
  plot(scalability$alibaba, type="o", ylim=g_range, axes=FALSE, ann=FALSE)
  title(plot_title)
  #title(ylab="Time/ms")
  mtext(text="Time (ms)", side=2, line=4)
  axis(1, at=1:6, lab=c("32", "64", "128", "256", "512", "1024"))
  axis(2, las=1, at=5000*0:g_range[2])
  title(xlab="Heap (MB)")
  box()
  lines(scalability$empire, type="o", pch=22, lty=2)
  lines(scalability$jopa, type="o", pch=23, lty=3)
  lines(scalability$komma, type="o", pch=24, lty=4)
  lines(scalability$rdfbeans, type="o", pch=25, lty=5)
  if (operation == "retrieve") {
    # Different placement of the legend so that it does not interfere with the plot lines
    legend(1, g_range[2]-20000, c("AliBaba", "Empire", "JOPA", "KOMMA", "RDFBeans"), pch=21:25, lty=1:5, bty="n")  
  } else if (operation == "retrieve-all") {
    # Different placement of the legend so that it does not interfere with the plot lines
    legend(1, g_range[2]-3000, c("AliBaba", "Empire", "JOPA", "KOMMA", "RDFBeans"), pch=21:25, lty=1:5, bty="n")  
  } else if (operation == "update") {
    legend(1, g_range[2]-12000, c("AliBaba", "Empire", "JOPA", "KOMMA", "RDFBeans"), pch=21:25, lty=1:5, bty="n")  
  } else {
    legend(1, g_range[2]-10000, c("AliBaba", "Empire", "JOPA", "KOMMA", "RDFBeans"), pch=21:25, lty=1:5, bty="n")
  }
}

##
# Creates heap-based scalability plots for each of the benchmark operations
#
# base_dir - directory containing raw benchmark data
# target_dir - directory into which the plots should be written
##
plot_scalability <- function(base_dir, target_dir) {
  setEPS()
  #OP1
  pdf(paste(target_dir, 'scalability-op1-appendix.pdf', sep=''))
  scalability_stats(base_dir, 'create', 'OP1 - Create')
  dev.off()
  
  #OP2
  pdf(paste(target_dir, 'scalability-op2-appendix.pdf', sep=''))
  scalability_stats(base_dir, 'create-batch', 'OP2 - Batch create')
  dev.off()
  
  #OP3
  pdf(paste(target_dir, 'scalability-op3-appendix.pdf', sep=''))
  scalability_stats(base_dir, 'retrieve', 'OP3 - Retrieve')
  dev.off()
  
  #OP4
  pdf(paste(target_dir, 'scalability-op4-appendix.pdf', sep=''))
  scalability_stats(base_dir, 'retrieve-all', 'OP4 - Retrieve all')
  dev.off()
  
  #OP5
  pdf(paste(target_dir, 'scalability-op5-appendix.pdf', sep=''))
  scalability_stats(base_dir, 'update', 'OP5 - Update')
  dev.off()
  
  #OP6
  pdf(paste(target_dir, 'scalability-op6-appendix.pdf', sep=''))
  scalability_stats(base_dir, 'delete', 'OP6 - Delete')
  dev.off()
}