{{/*
Standard Deployment for an HTTP service.
Usage in a service chart: {{ include "manabrew-lib.deployment" . }}
Expects values: image.repository, image.tag, replicaCount, containerPort,
env (map), envFromSecret (optional Secret name), resources,
probes.liveness / probes.readiness (HTTP paths).
*/}}
{{- define "manabrew-lib.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "manabrew-lib.fullname" . }}
  labels:
    {{- include "manabrew-lib.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      {{- include "manabrew-lib.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      labels:
        {{- include "manabrew-lib.labels" . | nindent 8 }}
    spec:
      securityContext:
        runAsNonRoot: true
      containers:
        - name: {{ include "manabrew-lib.fullname" . }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag | default .Chart.AppVersion }}"
          imagePullPolicy: {{ .Values.image.pullPolicy | default "IfNotPresent" }}
          ports:
            - name: http
              containerPort: {{ .Values.containerPort }}
          {{- with .Values.env }}
          env:
            {{- range $name, $value := . }}
            - name: {{ $name }}
              value: {{ $value | quote }}
            {{- end }}
          {{- end }}
          {{- with .Values.envFromSecret }}
          envFrom:
            - secretRef:
                name: {{ . }}
          {{- end }}
          livenessProbe:
            httpGet:
              path: {{ .Values.probes.liveness }}
              port: http
          readinessProbe:
            httpGet:
              path: {{ .Values.probes.readiness }}
              port: http
          {{- with .Values.resources }}
          resources:
            {{- toYaml . | nindent 12 }}
          {{- end }}
{{- end -}}
