const apiResponse = document.getElementById('api-response');

document.querySelectorAll('.tab-button').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.tab-button').forEach(x => x.classList.remove('active'));
        document.querySelectorAll('.tab-panel').forEach(x => x.classList.remove('active'));
        button.classList.add('active');
        document.getElementById(`tab-${button.dataset.tab}`).classList.add('active');
    });
});

async function api(path, options = {}) {
    const response = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    const text = await response.text();
    let body;
    try {
        body = text ? JSON.parse(text) : null;
    } catch {
        body = text;
    }
    apiResponse.textContent = JSON.stringify(body, null, 2);
    if (!response.ok) {
        throw new Error(body?.message || 'Request failed');
    }
    return body;
}

function populateSelect(select, items, valueKey, labelBuilder) {
    select.innerHTML = '';
    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueKey];
        option.textContent = labelBuilder(item);
        select.appendChild(option);
    });
}

async function loadReferenceData() {
    const [customers, facilities, products, bookings, rentals] = await Promise.all([
        api('/api/customers'),
        api('/api/facilities'),
        api('/api/products'),
        api('/api/service-bookings'),
        api('/api/rentals')
    ]);

    const servicePoints = facilities.filter(item => item.type === 'SERVICE_POINT');

    populateSelect(document.getElementById('customer-select'), customers, 'id', item => `${item.fullName} (${item.creditBalance})`);
    populateSelect(document.getElementById('service-point-select'), servicePoints, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('inventory-facility-select'), facilities, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('dispatch-source'), facilities, 'id', item => `${item.name} / ${item.city}`);
    populateSelect(document.getElementById('dispatch-target'), facilities, 'id', item => `${item.name} / ${item.city}`);

    // Pre-select warehouse as source and shop as target to avoid source===target default error
    const warehouse = facilities.find(f => f.type === 'WAREHOUSE');
    const shop = facilities.find(f => f.type === 'SHOP');
    if (warehouse) document.getElementById('dispatch-source').value = warehouse.id;
    if (shop) document.getElementById('dispatch-target').value = shop.id;
    populateSelect(document.getElementById('dispatch-product'), products, 'id', item => `${item.name} (${item.sku})`);
    populateSelect(document.getElementById('service-booking-select'), bookings, 'id', item => `${item.bookingNumber} / ${item.status}`);
    populateSelect(document.getElementById('service-intake-booking-select'), bookings, 'id', item => `${item.bookingNumber} / ${item.status}`);
    populateSelect(document.getElementById('service-action-booking-select'), bookings, 'id', item => `${item.bookingNumber} / ${item.status}`);
    populateSelect(document.getElementById('service-intake-product'), products, 'id', item => `${item.name} (${item.sku})`);
    populateSelect(document.getElementById('rental-select'), rentals, 'id', item => `${item.rentalNumber} / ${item.status}`);
    populateSelect(document.getElementById('feedback-rental-select'), rentals, 'id', item => `${item.rentalNumber} / ${item.status}`);

    document.getElementById('reference-output').textContent = JSON.stringify({ customers, facilities, products }, null, 2);
    document.getElementById('service-bookings-output').textContent = JSON.stringify(bookings, null, 2);
    document.getElementById('dispatch-output').textContent = JSON.stringify(await api('/api/expedition-requests'), null, 2);
    document.getElementById('rental-output').textContent = JSON.stringify(rentals, null, 2);
}

document.getElementById('refresh-reference-data').addEventListener('click', loadReferenceData);

// ── Rental wizard ──────────────────────────────────────────
let selectedBike = null;

function showWizardStep(n) {
    document.querySelectorAll('.wizard-panel').forEach(p => p.style.display = 'none');
    document.getElementById(`wizard-step-${n}`).style.display = 'block';
    [1, 2, 3].forEach(i => {
        const el = document.getElementById(`step-ind-${i}`);
        el.classList.remove('active', 'done');
        if (i < n) el.classList.add('done');
        if (i === n) el.classList.add('active');
    });
}

document.getElementById('load-bikes-button').addEventListener('click', async () => {
    const city = document.getElementById('bike-city-select').value;
    const bikes = await api(`/api/bikes?city=${encodeURIComponent(city)}`);
    const container = document.getElementById('bike-cards');
    container.innerHTML = '';
    if (bikes.length === 0) {
        container.innerHTML = '<p style="color:#64748b;margin:12px 0">No available bikes in this city.</p>';
        return;
    }
    bikes.forEach(bike => {
        const card = document.createElement('div');
        card.className = 'bike-card';
        card.innerHTML = `
            <div class="bike-card-model">${bike.modelName}</div>
            <div class="bike-card-meta">${bike.code} &middot; ${bike.category}</div>
            <div class="bike-card-meta">${bike.facilityName}</div>
            <div class="bike-card-price">&euro;${bike.pricePerMinute} / min</div>
        `;
        card.addEventListener('click', () => {
            document.querySelectorAll('.bike-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            selectedBike = bike;
            document.getElementById('step1-continue').disabled = false;
        });
        container.appendChild(card);
    });
});

document.getElementById('step1-continue').addEventListener('click', () => {
    if (!selectedBike) return;
    showWizardStep(2);
});

document.getElementById('step2-back').addEventListener('click', () => showWizardStep(1));

document.getElementById('step2-continue').addEventListener('click', () => {
    const minutes = Number(document.getElementById('rental-minutes').value);
    if (!minutes || minutes < 1) return;
    const customerEl = document.getElementById('customer-select');
    const customerName = customerEl.options[customerEl.selectedIndex]?.text ?? '';
    const estimatedPrice = (Number(selectedBike.pricePerMinute) * minutes).toFixed(2);
    document.getElementById('rental-summary').innerHTML = `
        <table>
            <tr><td>Bike</td><td>${selectedBike.modelName} (${selectedBike.code})</td></tr>
            <tr><td>Category</td><td>${selectedBike.category}</td></tr>
            <tr><td>Location</td><td>${selectedBike.facilityName}, ${selectedBike.city}</td></tr>
            <tr><td>Customer</td><td>${customerName}</td></tr>
            <tr><td>Planned minutes</td><td>${minutes}</td></tr>
            <tr><td>Estimated price</td><td>&euro;${estimatedPrice}</td></tr>
        </table>
    `;
    showWizardStep(3);
});

document.getElementById('step3-back').addEventListener('click', () => showWizardStep(2));

document.getElementById('step3-submit').addEventListener('click', async () => {
    const customerId = document.getElementById('customer-select').value;
    const plannedMinutes = Number(document.getElementById('rental-minutes').value);
    await api('/api/rentals/pre-register', {
        method: 'POST',
        body: JSON.stringify({ customerId, bikeId: selectedBike.id, plannedMinutes })
    });
    selectedBike = null;
    document.getElementById('bike-cards').innerHTML = '';
    document.getElementById('step1-continue').disabled = true;
    showWizardStep(1);
    await loadReferenceData();
});
// ───────────────────────────────────────────────────────────

document.getElementById('load-overview-button').addEventListener('click', async () => {
    const facilityId = document.getElementById('inventory-facility-select').value;
    const result = await api(`/api/inventory/overview?facilityId=${facilityId}`);
    document.getElementById('inventory-output').textContent = JSON.stringify(result, null, 2);
});

document.getElementById('load-sales-button').addEventListener('click', async () => {
    const facilityId = document.getElementById('inventory-facility-select').value;
    const result = await api(`/api/inventory/sales-analysis?facilityId=${facilityId}&days=30`);
    document.getElementById('inventory-output').textContent = JSON.stringify(result, null, 2);
});

document.getElementById('service-booking-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    await api('/api/service-bookings', {
        method: 'POST',
        body: JSON.stringify({
            customerName: form.get('customerName'),
            customerEmail: form.get('customerEmail'),
            bikeBrand: form.get('bikeBrand'),
            bikeModel: form.get('bikeModel'),
            problemDescription: form.get('problemDescription'),
            preferredFrom: new Date(form.get('preferredFrom')).toISOString(),
            preferredTo: new Date(form.get('preferredTo')).toISOString(),
            servicePointId: form.get('servicePointId')
        })
    });
    await loadReferenceData();
});

document.getElementById('service-status-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    const estimatedCompletion = form.get('estimatedCompletionAt');
    await api(`/api/service-bookings/${form.get('bookingId')}/status`, {
        method: 'PATCH',
        body: JSON.stringify({
            status: form.get('status'),
            preliminaryPrice: form.get('preliminaryPrice') || null,
            estimatedCompletionAt: estimatedCompletion ? new Date(estimatedCompletion).toISOString() : null,
            notes: form.get('notes') || null
        })
    });
    await loadReferenceData();
});

document.getElementById('service-intake-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    const partQuantity = Number(form.get('partQuantity'));
    const requiredParts = partQuantity > 0
        ? [{
            productId: form.get('productId'),
            requestedQuantity: partQuantity,
            unitPrice: Number(form.get('partUnitPrice') || 0)
        }]
        : [];

    await api(`/api/service-bookings/${form.get('bookingId')}/repair-intake`, {
        method: 'POST',
        body: JSON.stringify({
            technicalState: form.get('technicalState'),
            additionalFindings: form.get('additionalFindings') || null,
            workItems: [{
                description: form.get('workDescription'),
                laborPrice: Number(form.get('laborPrice') || 0)
            }],
            requiredParts,
            clientApproved: form.get('clientApproved') === 'on'
        })
    });
    await loadReferenceData();
});

document.getElementById('complete-service-button').addEventListener('click', async () => {
    const bookingId = document.getElementById('service-action-booking-select').value;
    await api(`/api/service-bookings/${bookingId}/complete`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('no-show-service-button').addEventListener('click', async () => {
    const bookingId = document.getElementById('service-action-booking-select').value;
    await api(`/api/service-bookings/${bookingId}/no-show`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('reject-service-button').addEventListener('click', async () => {
    const bookingId = document.getElementById('service-action-booking-select').value;
    await api(`/api/service-bookings/${bookingId}/reject-estimate`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('load-service-history-button').addEventListener('click', async () => {
    const email = document.getElementById('service-history-email').value;
    const history = await api(`/api/service-bookings/history?customerEmail=${encodeURIComponent(email)}`);
    document.getElementById('service-history-output').textContent = JSON.stringify(history, null, 2);
});

document.getElementById('dispatch-form').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    await api('/api/expedition-requests', {
        method: 'POST',
        body: JSON.stringify({
            sourceFacilityId: form.get('sourceFacilityId'),
            targetFacilityId: form.get('targetFacilityId'),
            priority: form.get('priority'),
            notes: form.get('notes'),
            items: [{
                productId: form.get('productId'),
                requestedQuantity: Number(form.get('requestedQuantity'))
            }]
        })
    });
    await loadReferenceData();
});


document.getElementById('start-rental-button').addEventListener('click', async () => {
    const rentalId = document.getElementById('rental-select').value;
    await api(`/api/rentals/${rentalId}/start`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('finish-rental-button').addEventListener('click', async () => {
    const rentalId = document.getElementById('rental-select').value;
    await api(`/api/rentals/${rentalId}/finish`, { method: 'POST' });
    await loadReferenceData();
});

document.getElementById('feedback-form').addEventListener('submit', async event => {
    event.preventDefault();
    const rentalId = document.getElementById('feedback-rental-select').value;
    await api(`/api/rentals/${rentalId}/feedback`, {
        method: 'POST',
        body: JSON.stringify({
            rating: Number(document.getElementById('feedback-rating').value),
            comment: document.getElementById('feedback-comment').value || null
        })
    });
    await loadReferenceData();
});

document.getElementById('report-issue-button').addEventListener('click', async () => {
    const rentalId = document.getElementById('rental-select').value;
    await api(`/api/rentals/${rentalId}/issue`, {
        method: 'POST',
        body: JSON.stringify({
            issueType: document.getElementById('issue-type-select').value,
            description: document.getElementById('issue-description').value
        })
    });
    await loadReferenceData();
});

loadReferenceData().catch(error => {
    apiResponse.textContent = error.message;
});
