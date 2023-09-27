package com.arr.simple.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class ExecuteTask<Params, Progress, Result> {

    private final Executor executor;

    public ExecuteTask() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void execute(final Params... params) {
        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        Result result = doInBackground(params);
                        onPostExecute(result);
                    }
                });
    }

    protected abstract Result doInBackground(Params... params);

    protected void onPostExecute(Result result) {
        // Implementa aquí la lógica que deseas ejecutar después de finalizar doInBackground
    }
}
