// server.js - Clinical Triage Backend API with Time Escalation
const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

let triageQueue = [];

// Helper to calculate initial clinical tier (1 = Critical, 2 = Emergent, 3 = Stable)
function calculateBaseTier(hr, bp, o2, rr, symptoms) {
    if (symptoms.chestPain || symptoms.stroke || symptoms.trauma) return 1;

    let score = 0;
    if (o2 < 95) score += (o2 >= 92) ? 1 : (o2 >= 88 ? 2 : 3);
    if (hr <= 50 || hr > 90) score += (hr <= 40 || hr > 130) ? 3 : ((hr <= 50 || hr > 110) ? 2 : 1);
    if (bp <= 100 || bp >= 130) score += (bp < 80 || bp >= 160) ? 3 : ((bp <= 90 || bp >= 140) ? 2 : 1);
    if (rr < 12 || rr > 20) score += (rr < 9 || rr > 24) ? 3 : ((rr < 12 || rr > 20) ? 2 : 1);

    if (score >= 5) return 1;
    if (score >= 2) return 2;
    return 3;
}

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/index.html');
});

// 1. GET ENDPOINT: Fetches sorted queue and dynamically computes escalation flags
app.get('/api/triage/:hospitalId', (req, res) => {
    const { hospitalId } = req.params;
    const now = Date.now();

    let filteredQueue = triageQueue.filter(patient => patient.hospital_id === hospitalId);

    // DYNAMIC TIME ESCALATION ENGINE
    filteredQueue = filteredQueue.map(patient => {
        const minutesWaiting = Math.floor((now - patient.time) / 60000);
        const escalationPoints = Math.floor(minutesWaiting / 20); // Upgrade tier priority score every 20 minutes

        let finalPriorityRank = patient.base_tier - escalationPoints;
        if (finalPriorityRank < 1) finalPriorityRank = 1;

        return {
            ...patient,
            current_priority: finalPriorityRank,
            minutes_waiting: minutesWaiting,
            is_escalated: escalationPoints > 0 && patient.base_tier > 1
        };
    });

    // Master Dynamic Sort
    filteredQueue.sort((a, b) => (a.current_priority !== b.current_priority) ? a.current_priority - b.current_priority : a.time - b.time);

    res.status(200).json(filteredQueue);
});

// 2. POST ENDPOINT: Commit incoming patient ticket
app.post('/api/triage/admit', (req, res) => {
    const { hospital_id, name, age, hr, bp, o2, rr, symptoms } = req.body;

    if (!hospital_id || !name || !age) {
        return res.status(400).json({ error: "Data rejected. Profile details missing." });
    }

    const baseTier = calculateBaseTier(hr, bp, o2, rr, symptoms || {});

    const newPatientTicket = {
        ticket_id: `TKT-${Math.floor(100000 + Math.random() * 900000)}`,
        hospital_id,
        name,
        age,
        hr,
        bp,
        o2,
        rr,
        symptoms: symptoms || {},
        base_tier: baseTier,
        time: Date.now()
    };

    triageQueue.push(newPatientTicket);
    res.status(201).json({ message: "Patient admitted successfully.", data: newPatientTicket });
});

// 3. POST ENDPOINT: Discharge highest prioritized case
app.post('/api/triage/discharge', (req, res) => {
    const { hospital_id } = req.body;
    const now = Date.now();
    let siteQueue = triageQueue.filter(p => p.hospital_id === hospital_id);

    if (siteQueue.length === 0) {
        return res.status(404).json({ error: "No patients currently in this queue channel." });
    }

    siteQueue.sort((a, b) => {
        const pA = Math.max(1, a.base_tier - Math.floor((now - a.time) / 60000 / 20));
        const pB = Math.max(1, b.base_tier - Math.floor((now - b.time) / 60000 / 20));
        return (pA !== pB) ? pA - pB : a.time - b.time;
    });

    const targetTicketId = siteQueue[0].ticket_id;
    const targetIndex = triageQueue.findIndex(p => p.ticket_id === targetTicketId);

    const removedPatient = triageQueue.splice(targetIndex, 1)[0];
    res.status(200).json({ message: "Patient record successfully cleared", patient: removedPatient });
});

app.listen(PORT, () => {
    console.log(`🏥 Triage Central Server active on: http://localhost:3000/`);
});